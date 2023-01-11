package com.example.messengerlite.fragments.chat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messengerlite.commontools.DateInputMask;
import com.example.messengerlite.R;
import com.example.messengerlite.commontools.Tools;
import com.example.messengerlite.adapters.InfoListAdapter;
import com.example.messengerlite.dialogs.AddLinkSheet;
import com.example.messengerlite.entities.UserEntity;
import com.example.messengerlite.entities.WebLinkEntity;
import com.example.messengerlite.interfaces.AddLinkListener;
import com.example.messengerlite.services.MediaService;
import com.example.messengerlite.services.UserService;
import com.example.messengerlite.viewmodels.MainViewModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditPersonalDataFragment extends DialogFragment
{
    private ImageButton backBtn;
    private EditText birthDayEditTxt, hometownEditTxt, workingCityEditTxt, displayNameEditTxt;
    private RecyclerView linkList;

    private MainViewModel mainViewModel;

    private List<Object> links;
    private int lastPos;

    private Context context;

    public EditPersonalDataFragment()
    {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.edit_personal_data_fragment, container, false);

        birthDayEditTxt = view.findViewById(R.id.birthDayEditTxt);
        hometownEditTxt = view.findViewById(R.id.hometownEditTxt);
        workingCityEditTxt = view.findViewById(R.id.workingEditTxt);
        displayNameEditTxt = view.findViewById(R.id.userDisplayNameEditTxt);

        linkList = view.findViewById(R.id.linkList);

        backBtn = view.findViewById(R.id.backBtn);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        if(savedInstanceState != null)
            lastPos = savedInstanceState.getInt("last pos");

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        links = new ArrayList<>();

        UserEntity user = mainViewModel.getMyUser();

        birthDayEditTxt.setText(Tools.convertToddmmyy(user.getBirthDay()));
        new DateInputMask(birthDayEditTxt);

        hometownEditTxt.setText(user.getHometown());
        workingCityEditTxt.setText(user.getWorkingCity());
        displayNameEditTxt.setText(user.getUserDisplayName());

        backBtn.setOnClickListener(v ->
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(context)
                    .setTitle("Save changes?")
                    .setMessage("Do you want to save all changes?")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i)
                        {
                            Date birthDay = Tools.reConvertddmmyy(birthDayEditTxt.getText().toString());

                            UserEntity userInfo = new UserEntity(
                                    displayNameEditTxt.getText().toString(),
                                    workingCityEditTxt.getText().toString(),
                                    hometownEditTxt.getText().toString(),
                                    birthDay);

                            mainViewModel.changePersonalData(userInfo);
                            UserService.service.updateInfo(mainViewModel.getMyUser()).enqueue(new Callback<Boolean>()
                            {
                                @Override
                                public void onResponse(Call<Boolean> call, Response<Boolean> response)
                                {

                                }

                                @Override
                                public void onFailure(Call<Boolean> call, Throwable t)
                                {
                                    Toast.makeText(context, "Failed to save your changes! :(", Toast.LENGTH_SHORT).show();
                                }
                            });

                            mainViewModel.reloadLinks();
                            dismiss();
                        }
                    })
                    .setNegativeButton("No, please discard them", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i)
                        {
                            mainViewModel.reloadLinks();
                            dismiss();
                        }
                    });
            builder.show();
        });

        linkList.setAdapter(new InfoListAdapter(links, new AddLinkListener()
        {
            @Override
            public void openLinkSheet()
            {
                WebLinkEntity link = ((InfoListAdapter)linkList.getAdapter())
                        .getPreContext(mainViewModel.getMyUser().getId());

                getChildFragmentManager().setFragmentResultListener("addLink", getActivity(),
                        new FragmentResultListener()
                        {
                            @Override
                            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result)
                            {
                                WebLinkEntity link = result.getParcelable("link");

                                ((InfoListAdapter) linkList.getAdapter()).insertNewLink(link);

                                MediaService.service.editLink(MediaService.SAVE, link).enqueue(new Callback<Integer>() {
                                    @Override
                                    public void onResponse(Call<Integer> call, Response<Integer> response) {
                                        ((InfoListAdapter) linkList.getAdapter()).getLastLink().setId(response.body());
                                    }

                                    @Override
                                    public void onFailure(Call<Integer> call, Throwable t) {
                                        Toast.makeText(context,
                                                "Can't save your link :(. Check your internet connection",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });

                new AddLinkSheet()
                        .addPreContext(link.getTitle(), link.getLink(), link.getId())
                        .show(getChildFragmentManager(), "add link");
            }

            @Override
            public void deleteLink()
            {
                WebLinkEntity link = ((InfoListAdapter)linkList.getAdapter()).getLastLink();
                ((InfoListAdapter)linkList.getAdapter()).deleteLastLink();

                MediaService.service.editLink(MediaService.DELETE, link).enqueue(new Callback<Integer>() {
                    @Override
                    public void onResponse(Call<Integer> call, Response<Integer> response) {

                    }

                    @Override
                    public void onFailure(Call<Integer> call, Throwable t)
                    {
                        Toast.makeText(context, "Can't delete your link! :(", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }, true, lastPos));

        linkList.setLayoutManager(new LinearLayoutManager(context));

        mainViewModel.observeInfoList().observe(getViewLifecycleOwner(), new Observer<List<Object>>() {
            @Override
            public void onChanged(List<Object> infos)
            {
                if(infos == null)
                    return;

                for (int i = 3; i < infos.size() - 1; i++)
                    links.add(((WebLinkEntity)infos.get(i)).clone());

                links.add(null);

                linkList.getAdapter().notifyDataSetChanged();
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
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        dialog.getWindow().setWindowAnimations(R.style.slideStyle);
        return dialog;
    }

    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);

        this.context = context;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putInt("last pos", ((InfoListAdapter)linkList.getAdapter()).getLastClickPos());
    }
}
