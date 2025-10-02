package org.swdc.llm;

import org.swdc.llama.core.LLamaCore;

public class LLMParameter {

    private int threads = Runtime.getRuntime().availableProcessors();

    private int batchSize = 1024 * 2;

    private int contextSize = 1024 * 2;

    private int seeds = LLamaCore.LLAMA_DEFAULT_SEED;

    private int gpuLayers = 0;

    private boolean memorySwap = false;

    private Float minP;

    private Long minKeepP;

    private Float temp;

    private Float topP;

    private Long minKeepTopP;

    private Integer topK;

    private ChatPrompt prompt;

    public ChatPrompt getPrompt() {
        return prompt;
    }

    public void setPrompt(ChatPrompt prompt) {
        this.prompt = prompt;
    }

    public Integer getTopK() {
        return topK;
    }

    public void setMinKeepTopP(Long minKeepTopP) {
        this.minKeepTopP = minKeepTopP;
    }

    public Float getTopP() {
        return topP;
    }

    public void setTopP(Float topP) {
        this.topP = topP;
    }

    public void setTopK(Integer topK) {
        this.topK = topK;
    }

    public Long getMinKeepTopP() {
        return minKeepTopP;
    }

    public boolean isMemorySwap() {
        return memorySwap;
    }

    public void setMemorySwap(boolean memorySwap) {
        this.memorySwap = memorySwap;
    }

    public int getGpuLayers() {
        return gpuLayers;
    }

    public void setGpuLayers(int gpuLayers) {
        this.gpuLayers = gpuLayers;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public int getContextSize() {
        return contextSize;
    }

    public void setContextSize(int contextSize) {
        this.contextSize = contextSize;
    }

    public Long getMinKeepP() {
        return minKeepP;
    }

    public void setMinKeepP(Long minKeepP) {
        this.minKeepP = minKeepP;
    }

    public Float getMinP() {
        return minP;
    }

    public void setMinP(Float minP) {
        this.minP = minP;
    }

    public int getSeeds() {
        return seeds;
    }

    public void setSeeds(int seeds) {
        this.seeds = seeds;
    }

    public Float getTemp() {
        return temp;
    }

    public void setTemp(Float temp) {
        this.temp = temp;
    }

    public int getThreads() {
        return threads;
    }

    public void setThreads(int threads) {
        this.threads = threads;
    }
}
