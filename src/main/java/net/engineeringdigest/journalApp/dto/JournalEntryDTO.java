package net.engineeringdigest.journalApp.dto;

import lombok.Builder;
import lombok.Data;
import net.engineeringdigest.journalApp.enums.Sentiment;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Data
@Builder
public class JournalEntryDTO {//Used for both: ,when taking in req body request (create/update), to send as response (fetch entries)  //You don’t want to expose DB-specific types like ObjectId, so therefore not present here
    private String title;
    private String content;
    private LocalDateTime date;
    private Sentiment sentiment;
}
