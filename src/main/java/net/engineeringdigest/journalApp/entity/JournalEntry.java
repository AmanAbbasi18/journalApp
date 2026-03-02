package net.engineeringdigest.journalApp.entity;

import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
@Document(collection = "journal_entries")
public class JournalEntry {   //called plane old java object
    @Id
    private ObjectId id;

    @NotNull
    private String title;

    private String content;

    private LocalDateTime date;
}
