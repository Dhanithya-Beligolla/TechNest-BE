package com.example.technestbe.mapper;

import com.example.technestbe.dto.quizes.MCQResponse;
import com.example.technestbe.dto.quizes.TutorialResponse;
import com.example.technestbe.model.quizes.MCQ;
import com.example.technestbe.model.quizes.Tutorial;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface QuizMapper {

    @Named("toTutorialResponseWithMcqs")
    TutorialResponse toTutorialResponse(Tutorial tutorial);

    @Named("toTutorialResponseWithoutMcqs")
    @Mapping(target = "mcqs", ignore = true)
    TutorialResponse toTutorialResponseWithoutMcqs(Tutorial tutorial);

    MCQResponse toMCQResponse(MCQ mcq);

    List<TutorialResponse> toTutorialResponseList(List<Tutorial> tutorials);
}