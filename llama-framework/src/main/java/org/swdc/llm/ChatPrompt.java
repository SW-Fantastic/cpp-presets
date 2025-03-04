package org.swdc.llm;

import java.util.List;

public interface ChatPrompt {

    String prompt(List<ChatMessage> messages, boolean addAss);

}
