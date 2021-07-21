package ando.repo.ui.recycler.diff;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import ando.repo.R;
import androidx.annotation.NonNull;

/**
 * Create adapter
 */
public class DiffUtilAdapter extends BaseQuickAdapter<DiffUtilDemoEntity, BaseViewHolder> {
    public static final int ITEM_0_PAYLOAD = 901;

    public DiffUtilAdapter(List<DiffUtilDemoEntity> list) {
        super(R.layout.layout_recycler_diff, list);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, DiffUtilDemoEntity item) {
        helper.setText(R.id.tweetName, item.getTitle())
                .setText(R.id.tweetText, item.getContent())
                .setText(R.id.tweetDate, item.getDate());
    }

    /**
     * This method will only be executed when there is payload info
     * <p>
     * 当有 payload info 时，只会执行此方法
     *
     * @param helper   A fully initialized helper.
     * @param item     The item that needs to be displayed.
     * @param payloads payload info.
     */
    @Override
    protected void convert(@NotNull BaseViewHolder helper, @NotNull DiffUtilDemoEntity item, @NotNull List<?> payloads) {
        for (Object p : payloads) {
            int payload = (int) p;
            if (payload == ITEM_0_PAYLOAD) {
                helper.setText(R.id.tweetName, item.getTitle())
                        .setText(R.id.tweetText, item.getContent());
            }
        }
    }
}
