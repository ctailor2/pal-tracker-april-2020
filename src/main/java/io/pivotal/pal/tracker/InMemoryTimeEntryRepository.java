package io.pivotal.pal.tracker;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTimeEntryRepository implements TimeEntryRepository {
    private final Map<Long, TimeEntry> timeEntries = new HashMap<>();
    private long id = 0;

    public TimeEntry create(TimeEntry timeEntry) {
        id++;
        TimeEntry newTimeEntry = new TimeEntry(
                id,
                timeEntry.getProjectId(),
                timeEntry.getUserId(),
                timeEntry.getDate(),
                timeEntry.getHours());
        timeEntries.put(id, newTimeEntry);
        return newTimeEntry;
    }

    public TimeEntry find(long timeEntryId) {
        return timeEntries.get(timeEntryId);
    }

    public List<TimeEntry> list() {
        return new ArrayList<>(timeEntries.values());
    }

    @Override
    public TimeEntry update(long timeEntryID, TimeEntry timeEntry) {
        if (!timeEntries.containsKey(id)) return null;
        TimeEntry updatedTimeEntry = new TimeEntry(
                id,
                timeEntry.getProjectId(),
                timeEntry.getUserId(),
                timeEntry.getDate(),
                timeEntry.getHours());
        timeEntries.put(id, updatedTimeEntry);
        return updatedTimeEntry;
    }

    @Override
    public void delete(long timeEntryId) {
        timeEntries.remove(id);
    }
}
