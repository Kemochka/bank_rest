package com.example.bankcards.controller;

import com.example.bankcards.dto.CardCreateDto;
import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.TransferRequestDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.StatusCode;
import com.example.bankcards.service.TransferService;
import com.example.bankcards.service.card.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;

@RestController
@RequestMapping("/api/card")
@RequiredArgsConstructor
public class CardController {
    private final CardService cardService;
    private final TransferService transferService;

    @Operation(
            summary = "Get Card by ID",
            description = "Get a card with all the data, including user data with an encrypted password",
            tags = {"Card", "getCardById"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = Card.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())})
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<Card> getCardById(@PathVariable Long id) {
        return cardService.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Create a Card",
            description = "Create a card for user, You can enter the user ID, card balance and the method will generate a card with a validity of 10 years",
            tags = {"Card", "createCard"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", content = {@Content(schema = @Schema(implementation = Card.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())})
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<CardCreateDto> createCard(@RequestBody CardCreateDto card) {
        cardService.createCard(card);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .buildAndExpand()
                .toUri();
        return ResponseEntity.status(HttpStatus.CREATED).location(uri).build();
    }

    @Operation(
            summary = "Update a Card status",
            description = "Update a card status for user, You can set the status on a bank card. It is necessary to enter the ID cards and set the status of the type of BLOCKED or ACTIVE",
            tags = {"Card", "updateCardByStatus"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", content = {@Content(schema = @Schema(implementation = Card.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema())})
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/{status}")
    public ResponseEntity<Card> updateCardByStatus(@PathVariable StatusCode status, @PathVariable Long id) {
        if (!cardService.updateStatusCard(id, status)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }


    @Operation(
            summary = "Transfer of money between your cards",
            description = "It is possible to transfer money between your cards, for this it will be necessary to introduce the card and the recipient card, as well as the amount of money transfer, and ID of User",
            tags = {"Card", "transfersCard"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", content = {@Content(schema = @Schema(implementation = Card.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())})
    })
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/transfers/{id}")
    public ResponseEntity<Void> transfersCard(@RequestBody TransferRequestDto transfers, @PathVariable Long id) {
        transferService.transferBetweenOwnCards(transfers, id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Get a balance for ID card",
            description = "Get a balance of ID card from the user",
            tags = {"Card", "getCardBalance"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = Card.class), mediaType = "application/json")}),
    })
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{id}/balance")
    public ResponseEntity<BigDecimal> getCardBalance(@PathVariable Long id) {
        BigDecimal balance = cardService.getCardBalance(id);
        return ResponseEntity.ok(balance);
    }

    @Operation(
            summary = "Get user's cards",
            description = "Retrieve a paginated list of cards for the specified user ID.",
            tags = {"Card", "UserCards"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = CardDto.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{userId}/cards")
    @PreAuthorize("hasRole('USER')")
    public Page<CardDto> getUserCards(
            @PathVariable Long userId,
            @PageableDefault(size = 5, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return cardService.getUserCards(userId, pageable);
    }

    @Operation(
            summary = "Delete card by ID",
            description = "Removing a card by ID. The response is no content",
            tags = {"Card", "delete"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema())})
    })
    @DeleteMapping("/{cardId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCard(@PathVariable Long cardId) {
        cardService.deleteCard(cardId);
        return ResponseEntity.noContent().build();
    }
}
