package ru.makhorin.userservice.domain.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

// Сущность для работы с MongoDB
@Document(collection = "userRequisites")
@Data
@Builder(toBuilder = true)
public class UserRequisites {
    @Id
    private String id;
    @Indexed(unique = true)
    private String userId;
    private List<Account> accounts;
}
