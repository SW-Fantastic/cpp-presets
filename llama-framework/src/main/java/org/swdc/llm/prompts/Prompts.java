package org.swdc.llm.prompts;

import org.swdc.llm.ChatMessage;
import org.swdc.llm.ChatPrompt;

/**
 * 这个是LLama.cpp的llama-chat.cpp中的llm_chat_detect_template
 * 函数以及llm_chat_apply_template的java实现。
 *
 * 之所以使用java重新实现它，是因为C++层面经常出现奇怪乱码问题，转写后使用起来
 * 更加简洁，也更加稳定。
 *
 * PS：作者试了一下通过JinJava库使用模型自带的Jinja模板，但是感觉不如这种
 * 简单的字符串拼接，效果相当不理想，所以本框架以后基本上全都自己拼接prompt了，
 * 不使用模板进行渲染。
 *
 */
public interface Prompts {


    /**
     * 该Prompt可能有问题。
     */
    ChatPrompt RwKVWorld = (messages, addAss) -> {
        StringBuilder result = new StringBuilder();
        for (ChatMessage message : messages) {
            PromptRole role = message.getRole();
            String text = message.getContent();
            if (role.equals(PromptRole.USER)) {
                result.append(String.format("User: %s\n\nAssistant: \n\n",text));
            } else {
                result.append(String.format("%s\n\n",text));
            }
        }
        return result.toString();
    };





    /**
     * 参考 llm_chat_detect_template函数实现本方法。
     * @param prompt 模型提示模板
     * @return 提示词生成器。
     */
    static ChatPrompt getByModel(String prompt) {
        if(prompt == null) {
            return null;
        }
        if (prompt.contains("<|im_start|>")) {
            if (prompt.contains("<|im_sep|>")) {
                return ChatMLPrompts.ChatML_SEP;
            }
            return ChatMLPrompts.ChatML;
        }
        if (prompt.contains("<|assistant|>") && prompt.contains("<|end|>")) {
            return ChatMLPrompts.PhiV3;
        }
        if (prompt.contains("### Instruction:") && prompt.contains("<|EOT|>")) {
            return DeepSeekPrompts.DeepSeek;
        }
        if(prompt.contains("'Assistant: ' + message['content'] + eos_token")) {
            return DeepSeekPrompts.DeepSeekV2;
        }
        if (prompt.contains("<｜Assistant｜>") && prompt.contains("<｜User｜>") && prompt.contains("<｜end▁of▁sentence｜>")) {
            return DeepSeekPrompts.DeepSeekV3;
        }
        if (prompt.contains("[gMASK]<sop>")) {
            return GLMPrompts.ChatGLMV4;
        }
        if (prompt.contains("[gMASK]sop")) {
            return GLMPrompts.ChatGLMV3;
        }
        if (prompt.contains("rwkv-world")) {
            return RwKVWorld;
        }
        if(prompt.contains("mistral") || prompt.contains("[INST]")) {
            if (prompt.contains("[SYSTEM_PROMPT]")) {
                return MistralPrompts.MistralV7;
            } else if (
                // catches official 'v1' template
                prompt.contains("' [INST] ' + system_message") ||
                // catches official 'v3' and 'v3-tekken' templates
                prompt.contains("[AVAILABLE_TOOLS]")) {
                // Official mistral 'v1', 'v3' and 'v3-tekken' templates
                // See: https://github.com/mistralai/cookbook/blob/main/concept-deep-dive/tokenization/chat_templates.md
                // See: https://github.com/mistralai/cookbook/blob/main/concept-deep-dive/tokenization/templates.md
                if (prompt.contains(" [INST]")) {
                    return MistralPrompts.MistralV1;
                } else if (prompt.contains("\"[INST]\"")) {
                    return MistralPrompts.MistralV3Tekken;
                }
                return MistralPrompts.MistralV3;
            } else {
                // llama2 template and its variants
                // [variant] support system message
                // See: https://huggingface.co/blog/llama2#how-to-prompt-llama-2
                if (LLamaPrompts.needStripeMessage(prompt)) {
                    return LLamaPrompts.LLAMA2_SYS_STRIP;
                } else if (LLamaPrompts.supportSystemMessage(prompt)) {
                    return LLamaPrompts.LLAMA2_SYS;
                } else if (LLamaPrompts.bosInsideHistory(prompt)) {
                    return LLamaPrompts.LLAMA2_SYS_BOS;
                } else {
                    return LLamaPrompts.LLAMA2;
                }
            }
        }
        if (prompt.contains("<|start_header_id|>") && prompt.contains("<|end_header_id|>")) {
            return LLamaPrompts.LLAMA3;
        }
        if (prompt.contains("<start_of_turn>")) {
            return GoogleGemmaPrompts.GoogleGemma;
        }
        throw new IllegalArgumentException("Unknown prompt format: " + prompt);
    }

}
