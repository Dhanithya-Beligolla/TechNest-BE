package com.example.technestbe.service.advertisement;

import com.example.technestbe.dto.advertisement.AdvertisementRequestDto;
import com.example.technestbe.dto.advertisement.AdvertisementResponseDto;
import com.example.technestbe.model.advertisement.Advertisement;
import com.example.technestbe.model.user.User;
import com.example.technestbe.repo.advertisement.AdvertisementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdvertisementService {

    private final AdvertisementRepository advertisementRepository;

    public AdvertisementResponseDto createAdvertisement(AdvertisementRequestDto requestDto, User user) {
        Advertisement advertisement = new Advertisement();
        advertisement.setTitle(requestDto.getTitle());
        advertisement.setDescription(requestDto.getDescription());
        advertisement.setCategory(requestDto.getCategory());
        advertisement.setReferenceName(requestDto.getReferenceName());
        advertisement.setLink(requestDto.getLink());
        advertisement.setUser(user);

        Advertisement savedAdvertisement = advertisementRepository.save(advertisement);
        return mapToDto(savedAdvertisement);
    }

    public List<AdvertisementResponseDto> getAllAdvertisements() {
        return advertisementRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<AdvertisementResponseDto> getAdvertisementsByCategory(String category) {
        return advertisementRepository.findByCategory(category)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<AdvertisementResponseDto> getUserAdvertisements(User user) {
        return advertisementRepository.findByUserId(user.getId())
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public AdvertisementResponseDto updateAdvertisement(Long id, AdvertisementRequestDto requestDto, User user) {
        Advertisement advertisement = advertisementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Advertisement not found"));

        if (!advertisement.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You are not authorized to update this advertisement");
        }

        advertisement.setTitle(requestDto.getTitle());
        advertisement.setDescription(requestDto.getDescription());
        advertisement.setCategory(requestDto.getCategory());
        advertisement.setReferenceName(requestDto.getReferenceName());
        advertisement.setLink(requestDto.getLink());

        Advertisement updatedAdvertisement = advertisementRepository.save(advertisement);
        return mapToDto(updatedAdvertisement);
    }

    public void deleteAdvertisement(Long id, User user) {
        Advertisement advertisement = advertisementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Advertisement not found"));

        if (!advertisement.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You are not authorized to delete this advertisement");
        }

        advertisementRepository.delete(advertisement);
    }

    private AdvertisementResponseDto mapToDto(Advertisement advertisement) {
        AdvertisementResponseDto responseDto = new AdvertisementResponseDto();
        responseDto.setId(advertisement.getId());
        responseDto.setTitle(advertisement.getTitle());
        responseDto.setDescription(advertisement.getDescription());
        responseDto.setCategory(advertisement.getCategory());
        responseDto.setReferenceName(advertisement.getReferenceName());
        responseDto.setLink(advertisement.getLink());
        responseDto.setCreatedBy(advertisement.getUser().getEmail());
        return responseDto;
    }
}
