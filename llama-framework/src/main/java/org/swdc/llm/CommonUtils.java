package org.swdc.llm;

import org.bytedeco.javacpp.FloatPointer;
import org.bytedeco.javacpp.IntPointer;
import org.swdc.llama.core.LLamaCore;
import org.swdc.llama.core.llama_batch;

public class CommonUtils {

    public static final int NORMALIZE_NONE = -1;
    public static final int NORMALIZE_MAX_ABS = 0;
    public static final int NORMALIZE_EUCLIDEAN = 2;


    /**
     * 创建一个新的Batch，用于解码。
     * @param tokens 一个包含TokenId的BytePointer对象。
     * @param n_tokens Token的数量。
     * @param seq_id 序列ID，用于区分不同的输入（默认为0）
     * @return 一个初始化的Batch对象。
     */
    public static llama_batch createBatch(IntPointer tokens,int posBase, int batchSize, int n_tokens, int seq_id) {

        llama_batch llamaBatch = LLamaCore.llama_batch_init(batchSize,0,1);
        return fillBatch(llamaBatch, tokens, posBase, n_tokens, seq_id);

    }

    /**
     * 重置Batch，将所有TokenId设置为0。
     * @param batch 要重置的Batch对象。
     * @return 重置后的Batch对象。
     */
    public static llama_batch resetBatch(llama_batch batch) {
        batch.n_tokens(0);
        return batch;
    }

    /**
     * 填充Batch对象，用于解码。
     * @param llamaBatch Batch对象
     * @param tokens 一个包含TokenId的BytePointer对象。
     * @param n_tokens Token的数量。
     * @param seq_id 序列ID，用于区分不同的输入（默认为0）
     * @return 填充后的Batch对象。
     */
    public static llama_batch fillBatch(llama_batch llamaBatch, IntPointer tokens, int posBase, int n_tokens, int seq_id) {
        int batchOffset = llamaBatch.n_tokens();
        for (int i = 0; i < n_tokens; i++) {

            // 在Batch中填入TokenId
            llamaBatch.token().put(batchOffset + i,tokens.get(i));
            // 在Batch中填入Token的位置
            llamaBatch.pos().put(batchOffset + i,posBase + i);
            // 在Batch中填入序列ID
            llamaBatch.seq_id(batchOffset + i).put(0,seq_id);
            // 在Batch中填入是否为最后一个Token的标志位
            llamaBatch.logits().put(batchOffset + i, i == n_tokens - 1 ? (byte) 1: (byte) 0);
            // 在Batch填入该Token属于几个序列（默认为1）
            llamaBatch.n_seq_id().put(batchOffset + i,1);

        }
        llamaBatch.n_tokens(batchOffset + n_tokens);
        return llamaBatch;
    }

    public static llama_batch fillEmbdBatch(llama_batch llamaBatch, IntPointer tokens, int posBase, int n_tokens, int seq_id) {

        int batchOffset = llamaBatch.n_tokens();
        for (int i = 0; i < n_tokens; i++) {

            // 在Batch中填入TokenId
            llamaBatch.token().put(batchOffset + i,tokens.get(i));
            // 在Batch中填入Token的位置
            llamaBatch.pos().put(batchOffset + i,posBase + i);
            // 在Batch中填入序列ID
            llamaBatch.seq_id(batchOffset + i).put(0,seq_id);
            // 在Batch中填入是否为最后一个Token的标志位
            llamaBatch.logits().put( batchOffset + i, (byte) 1);
            // 在Batch填入该Token属于几个序列（默认为1）
            llamaBatch.n_seq_id().put(batchOffset + i,1);

        }
        llamaBatch.n_tokens( batchOffset + n_tokens );
        return llamaBatch;
    }



    public static void normalize(FloatPointer input, FloatPointer output,int count,int embdNormalize) {

        double sum = 0;
        switch (embdNormalize) {
            case NORMALIZE_NONE: {
                sum = 1;
                break;
            }
            case NORMALIZE_MAX_ABS: {
                for (int i = 0; i < count; i++) {
                    float val = input.get(i);
                    if (sum < Math.abs(val)) {
                        sum = Math.abs(val);
                    }
                }
                sum = sum / 32760.0;
                break;
            }
            case NORMALIZE_EUCLIDEAN: {
                for (int i = 0; i < count; i++) {
                    float val = input.get(i);
                    sum = sum + val * val;
                }
                sum = Math.sqrt(sum);
                break;
            }
            default:{
                for (int i = 0; i < count; i++) {
                    float val = input.get(i);
                    sum = sum + Math.pow(Math.abs(val),embdNormalize);
                }
                sum = Math.pow(sum,1.0/embdNormalize);
                break;
            }
        }

        float normal = (float)( sum > 0 ? 1.0f / sum : 0f );
        for (int i = 0; i < count; i++) {
            output.put(i,input.get(i) * normal);
        }

    }

}
