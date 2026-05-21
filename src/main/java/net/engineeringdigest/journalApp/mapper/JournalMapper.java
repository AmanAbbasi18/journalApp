package net.engineeringdigest.journalApp.mapper;

import net.engineeringdigest.journalApp.dto.JournalEntryDTO;
import net.engineeringdigest.journalApp.entity.JournalEntry;

public class JournalMapper {

    public static JournalEntry toEntity(JournalEntryDTO dto) {
        JournalEntry entry = new JournalEntry();
        entry.setTitle(dto.getTitle());
        entry.setContent(dto.getContent());
        return entry;
    }

    public static JournalEntryDTO toDTO(JournalEntry entry) {
        return JournalEntryDTO.builder()
                .title(entry.getTitle())
                .content(entry.getContent())
                .date(entry.getDate())
                .sentiment(entry.getSentiment())
                .build();
    }
}
