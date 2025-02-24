package org.swdc.llama.ext;

import java.util.List;

public interface ChatPrompt {

    String prompt(List<ChatMessage> messages, boolean addAss);

}
