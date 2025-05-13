package ru.makhorin.userservice.domain.mapper;

import ru.makhorin.userservice.domain.dto.AccountDto;
import ru.makhorin.userservice.domain.dto.CardDto;
import ru.makhorin.userservice.domain.dto.UserRequisitesDto;
import ru.makhorin.userservice.domain.entity.Account;
import ru.makhorin.userservice.domain.entity.Card;
import ru.makhorin.userservice.domain.entity.UserRequisites;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserRequisitesMapper {

    UserRequisites toEntity(UserRequisitesDto dto);
    UserRequisitesDto toDto(UserRequisites entity);

    Account toAccount(AccountDto dto);
    AccountDto toAccountDto(Account account);

    Card toCard(CardDto dto);
    CardDto toCardDto(Card card);
}
