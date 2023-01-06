package com.example.messengerlite.fragments.authentication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.messengerlite.AuthenticationState;
import com.example.messengerlite.R;
import com.example.messengerlite.adapters.RegisterPagerAdapter;
import com.example.messengerlite.interfaces.ChangeAuthenticationSceneListener;
import com.example.messengerlite.services.UserService;
import com.example.messengerlite.viewmodels.MainViewModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterFragment extends DialogFragment
{
    private TextView currentPageTxt;
    private ImageButton backBtn;
    private Button nextBtn;
    private ViewPager2 registerPager;

    private MainViewModel mainViewModel;

    private ChangeAuthenticationSceneListener listener;
    private Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.register_fragment, container, false);

        currentPageTxt = view.findViewById(R.id.textView);
        backBtn = view.findViewById(R.id.backBtn);
        nextBtn = view.findViewById(R.id.nextBtn);
        registerPager = view.findViewById(R.id.registerPager);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);

        registerPager.setUserInputEnabled(false);
        registerPager.setAdapter(new RegisterPagerAdapter(this));
        nextBtn.setOnClickListener(v ->
        {
            mainViewModel.incrementRegisterState();
        });
        backBtn.setOnClickListener(v ->
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                    .setTitle("Bạn có muốn hủy quá trình đăng ký")
                    .setMessage("Toàn bộ quá trình đăng ký sẽ bị hủy!")
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i)
                        {

                        }
                    })
                    .setPositiveButton("OK", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i)
                        {
                            listener.changeState(AuthenticationState.USER_NAME);
                            mainViewModel.clearRegistrationState();
                            dismiss();
                        }
                    });
            builder.show();
        });

        mainViewModel.observeRegisterState().observe(getViewLifecycleOwner(), new Observer<Integer>()
        {
            @Override
            public void onChanged(Integer state)
            {
                registerPager.setCurrentItem(state);

                if(state == RegisterPagerAdapter.LOGIN_INFO)
                    currentPageTxt.setText("Thông tin đăng nhập");
                else if(state == RegisterPagerAdapter.BIRTHDAY)
                    currentPageTxt.setText("Sinh nhật của bạn");
                else if(state == RegisterPagerAdapter.AVATAR)
                    currentPageTxt.setText("Ảnh trang cá nhân");
                else
                {
                    listener.changeState(AuthenticationState.USER_NAME);
                    UserService.service.register(mainViewModel.getNewUser()).enqueue(new Callback<Boolean>() {
                        @Override
                        public void onResponse(Call<Boolean> call, Response<Boolean> response)
                        {
                            Toast.makeText(context, "Registration complete!", Toast.LENGTH_SHORT).show();
                            listener.changeState(AuthenticationState.USER_NAME);
                            dismiss();
                        }

                        @Override
                        public void onFailure(Call<Boolean> call, Throwable t)
                        {
                            Toast.makeText(context, "Check your internet connection!", Toast.LENGTH_SHORT).show();
                        }
                    });

                    mainViewModel.clearRegistrationState();
                }
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setStyle(STYLE_NO_TITLE, R.style.FullScreenDialogStyle);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
    {
        Dialog dialog = new Dialog(getActivity(), getTheme()){
            @Override
            public void onBackPressed()
            {
                listener.changeState(AuthenticationState.USER_NAME);
                dismiss();
            }
        };

        dialog.getWindow().setWindowAnimations(R.style.fadeStyle);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        return dialog;
    }

    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);

        listener = (ChangeAuthenticationSceneListener) context;
        this.context = context;
    }
}
