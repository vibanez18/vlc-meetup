package com.alexa;
import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.model.Intent;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.RequestEnvelope;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.ui.SimpleCard;
import com.amazon.ask.model.ui.SsmlOutputSpeech;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
class FallbackIntentHandlerTest {

    @Inject
    private FallbackIntentHandler handler;

    @Test
    void testFallbackIntentHandler() {
        HandlerInput input = HandlerInput.builder()
                .withRequestEnvelope(RequestEnvelope.builder()
                        .withRequest(IntentRequest.builder()
                                .withIntent(Intent.builder()
                                        .withName("AMAZON.FallbackIntent")
                                        .build())
                                .build())
                        .build()
                ).build();
        assertTrue(handler.canHandle(input));
        Optional<Response> responseOptional = handler.handle(input);
        assertTrue(responseOptional.isPresent());
        Response response = responseOptional.get();
        assertTrue(response.getOutputSpeech() instanceof SsmlOutputSpeech);
        String speechText = "Sorry, I don't know that. You can say try saying help!";
        String expectedSsml = "<speak>" + speechText + "</speak>";
        assertEquals(expectedSsml, ((SsmlOutputSpeech) response.getOutputSpeech()).getSsml());

        assertNotNull(response.getReprompt().getOutputSpeech());
        assertTrue(response.getReprompt().getOutputSpeech() instanceof SsmlOutputSpeech);
        assertEquals(expectedSsml,((SsmlOutputSpeech) response.getReprompt().getOutputSpeech()).getSsml());
        assertTrue(response.getCard() instanceof SimpleCard);
        assertEquals("HelloWorld", ((SimpleCard) response.getCard()).getTitle());
        assertEquals(speechText, ((SimpleCard) response.getCard()).getContent());
        assertFalse(response.getShouldEndSession());
    }
}
