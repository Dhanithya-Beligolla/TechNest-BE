package com.example.technestbe.controller.advertisement;

import com.example.technestbe.dto.advertisement.AdvertisementRequestDto;
import com.example.technestbe.dto.advertisement.AdvertisementResponseDto;
import com.example.technestbe.model.user.User;
import com.example.technestbe.service.advertisement.AdvertisementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/advertisements")
@RequiredArgsConstructor
public class AdvertisementController {

    private final AdvertisementService advertisementService;

    @PostMapping
    public ResponseEntity<AdvertisementResponseDto> createAdvertisement(
            @RequestBody AdvertisementRequestDto requestDto,
            @AuthenticationPrincipal User user) {
        AdvertisementResponseDto responseDto = advertisementService.createAdvertisement(requestDto, user);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping
    public ResponseEntity<List<AdvertisementResponseDto>> getAllAdvertisements() {
        List<AdvertisementResponseDto> advertisements = advertisementService.getAllAdvertisements();
        return ResponseEntity.ok(advertisements);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<AdvertisementResponseDto>> getAdvertisementsByCategory(
            @PathVariable String category) {
        List<AdvertisementResponseDto> advertisements = advertisementService.getAdvertisementsByCategory(category);
        return ResponseEntity.ok(advertisements);
    }

    @GetMapping("/my-ads")
    public ResponseEntity<List<AdvertisementResponseDto>> getUserAdvertisements(
            @AuthenticationPrincipal User user) {
        List<AdvertisementResponseDto> advertisements = advertisementService.getUserAdvertisements(user);
        return ResponseEntity.ok(advertisements);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdvertisementResponseDto> updateAdvertisement(
            @PathVariable Long id,
            @RequestBody AdvertisementRequestDto requestDto,
            @AuthenticationPrincipal User user) {
        AdvertisementResponseDto responseDto = advertisementService.updateAdvertisement(id, requestDto, user);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdvertisement(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        advertisementService.deleteAdvertisement(id, user);
        return ResponseEntity.noContent().build();
    }
}
