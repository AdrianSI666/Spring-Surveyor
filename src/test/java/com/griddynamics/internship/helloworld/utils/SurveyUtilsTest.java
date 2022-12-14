package com.griddynamics.internship.helloworld.utils;

import com.griddynamics.internship.helloworld.repository.SurveyRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class SurveyUtilsTest {
    @Mock
    private SurveyRepository surveyRepository;

    @InjectMocks
    private SurveyUtils surveyUtils;

    //Ignored for now since this method will change shortly when JPASpecifications will be merged.
    /*@Test
    void test_generated_String() {
        given(surveyRepository.existsSurveyByPasscode(any())).willReturn(false);

        //testing multiple random passcodes
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(surveyUtils).generatePasscode(argumentCaptor.capture());
        Question capturedQuestion = argumentCaptor.getValue();

        assertEquals(capturedQuestion.getName(), questionReceiverDTO.name());
        assertEquals(capturedQuestion.getContent(), questionReceiverDTO.content());

        assertEquals(capturedQuestion.getSurvey().getId(), questionReceiverDTO.surveyId());
    }*/

}
