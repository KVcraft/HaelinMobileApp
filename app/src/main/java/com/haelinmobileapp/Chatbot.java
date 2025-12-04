package com.haelinmobileapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.haelinmobileapp.retrofit.ApiService;
import com.haelinmobileapp.retrofit.ChatMessage;
import com.haelinmobileapp.retrofit.ChatReponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Chatbot extends Fragment {

    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> messages = new ArrayList<>();

    private ApiService apiService;  // Your existing Retrofit interface

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8000/") // trailing slash
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chatbot, container, false);

        recyclerView = view.findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        chatAdapter = new ChatAdapter(messages);
        recyclerView.setAdapter(chatAdapter);

        // Add initial bot greeting
        messages.add(new ChatMessage("Hey! Ask me anything about health.", false));
        chatAdapter.notifyDataSetChanged();

        EditText editText = view.findViewById(R.id.txt_type_message);
        ImageView sendBtn = view.findViewById(R.id.btnChatSend);

        sendBtn.setOnClickListener(v -> {
            String message = editText.getText().toString().trim();

            if (!message.isEmpty()) {
                // Send the message to chatbot (call your method here)
                sendMessageToChatbot(message);

                // Clear the input box after sending
                editText.setText("");
            }
        });

        return view;
    }

    public void sendMessageToChatbot(String userInput) {
        // Add user message
        messages.add(new ChatMessage(userInput, true));
        chatAdapter.notifyItemInserted(messages.size() - 1);
        recyclerView.scrollToPosition(messages.size() - 1);

        // Call your existing ApiService method
        Call<ChatReponse> call = apiService.chat(new UserMessage(userInput));
        call.enqueue(new Callback<ChatReponse>() {
            @Override
            public void onResponse(Call<ChatReponse> call, Response<ChatReponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String botReply = response.body().getReply();
                    if (getActivity() == null) return;
                    getActivity().runOnUiThread(() -> {
                        messages.add(new ChatMessage(botReply, false));
                        chatAdapter.notifyItemInserted(messages.size() - 1);
                        recyclerView.scrollToPosition(messages.size() - 1);
                    });
                }
            }

            @Override
            public void onFailure(Call<ChatReponse> call, Throwable t) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }
        });
    }
}
