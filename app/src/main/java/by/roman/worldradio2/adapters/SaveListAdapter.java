package by.roman.worldradio2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

import by.roman.worldradio2.R;
import by.roman.worldradio2.dataclasses.SaveCardItem;

public class SaveListAdapter extends RecyclerView.Adapter<SaveListAdapter.ViewHolder> {
    private List<SaveCardItem> cards;
    private Context context;
    private OnItemClickListener listener;

    public SaveListAdapter(Context context, List<SaveCardItem> cards, OnItemClickListener listener) {
        this.context = context;
        this.cards = cards;
        this.listener = listener;
    }
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_save_card,parent,false);
        return new ViewHolder(view);
    }
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SaveCardItem card = cards.get(position);

        // Устанавливаем текст
        holder.nameStation.setText(card.getNameStation());

        // Загружаем картинку с помощью Glide
        Glide.with(context)
                .load(card.getLogoURL())
                .into(holder.logoStation);

        Glide.with(context)
                .load(R.drawable.delete_save)
                .into(holder.delete);


        // Обрабатываем нажатие на элемент списка
        holder.itemView.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition(); // Берём актуальную позицию
            if (adapterPosition == RecyclerView.NO_POSITION) return;

            for (SaveCardItem item : cards) {
                item.setPlaying(false);
            }
            cards.get(adapterPosition).setPlaying(true);
            notifyDataSetChanged();
            if(listener != null){
                listener.onItemClick(position);
            }
        });
    }


    public int getItemCount() {
        return cards.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameStation;
        ImageView logoStation, delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameStation = itemView.findViewById(R.id.nameStationView);
            logoStation = itemView.findViewById(R.id.logoStationView);
            delete = itemView.findViewById(R.id.soundView);
        }
    }
}
