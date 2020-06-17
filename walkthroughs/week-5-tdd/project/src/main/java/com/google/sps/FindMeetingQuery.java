// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Arrays;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    //Edge case if meeting request duration is invalid
    if (request.getDuration() >= 24*60) {
      return new ArrayList<TimeRange>();
    }

    Collection<TimeRange> availableMeetingTimes = new HashSet<>();

    //initialize the possible time range to encompass the whole day
    TimeRange possibleRange = TimeRange.fromStartEnd(TimeRange.START_OF_DAY, TimeRange.END_OF_DAY, true);
    
    //Initialize sets for conflicts from both mandatory and optional attendees
    Collection<TimeRange> conflictsSet = new HashSet<>();
    Collection<TimeRange> optAttendeesConflictsSet = new HashSet<>();

    //Go through each event to check for conflicts
    for (Event event: events) {
      if (!Collections.disjoint(request.getAttendees(), event.getAttendees())) {
        conflictsSet.add(event.getWhen());
      }
      if (!Collections.disjoint(request.getOptionalAttendees(), event.getAttendees())) {
        optAttendeesConflictsSet.add(event.getWhen());
      }
    }

    //Edge case of no conflicts
    if (conflictsSet.size() + optAttendeesConflictsSet.size() == 0) {
      return Arrays.asList(TimeRange.WHOLE_DAY);
    }

    List<TimeRange> conflictsList = new ArrayList<>(conflictsSet);
    Collections.sort(conflictsList, TimeRange.ORDER_BY_START);

    //Find valid meeting times in the day by going through found conflicts
    for (TimeRange conflict: conflictsList) {
      if (conflict.overlaps(possibleRange)) {
        if (possibleRange.start() < conflict.start()) {
          TimeRange availableTime = TimeRange.fromStartEnd(possibleRange.start(), conflict.start(), false);
          if (availableTime.duration() >= request.getDuration()) {
            availableMeetingTimes.add(availableTime);
          }
        }
        possibleRange = TimeRange.fromStartEnd(conflict.end(), TimeRange.END_OF_DAY, true);
      }
    }
    
    //check if the possible range that was left is a valid time to add
    if (possibleRange.duration() >= request.getDuration() && !possibleRange.equals(TimeRange.WHOLE_DAY)) {
      availableMeetingTimes.add(possibleRange);
    }

    //Same process as above but combined with optional attendees
    if (request.getOptionalAttendees().size() > 0) {
      possibleRange = TimeRange.fromStartEnd(TimeRange.START_OF_DAY, TimeRange.END_OF_DAY, true);

      Collection<TimeRange> allConflictsSet = new HashSet<>();
      allConflictsSet.addAll(conflictsSet);
      allConflictsSet.addAll(optAttendeesConflictsSet);
      List<TimeRange> allConflictsList = new ArrayList<>(allConflictsSet);
      Collections.sort(allConflictsList, TimeRange.ORDER_BY_START);

      Collection<TimeRange> allAvailableMeetingTimes = new HashSet<>();

      //Find valid meeting times in the day by going through found conflicts
      for (TimeRange conflict: allConflictsList) {
        if (conflict.overlaps(possibleRange)) {
          if (possibleRange.start() < conflict.start()) {
            TimeRange availableTime = TimeRange.fromStartEnd(possibleRange.start(), conflict.start(), false);
            if (availableTime.duration() >= request.getDuration()) {
              allAvailableMeetingTimes.add(availableTime);
            }
          }
          possibleRange = TimeRange.fromStartEnd(conflict.end(), TimeRange.END_OF_DAY, true);
        }
      }

      //check if the possible range that was left is a valid time to add
      if (possibleRange.duration() >= request.getDuration() && !possibleRange.equals(TimeRange.WHOLE_DAY)) {
        allAvailableMeetingTimes.add(possibleRange);
      }

      if (allAvailableMeetingTimes.size() > 0) {
        availableMeetingTimes = allAvailableMeetingTimes;
      }
    }
    
    List<TimeRange> meetingsAsList = new ArrayList<>(availableMeetingTimes);
    Collections.sort(meetingsAsList, TimeRange.ORDER_BY_START);

    return meetingsAsList;
  }
}
