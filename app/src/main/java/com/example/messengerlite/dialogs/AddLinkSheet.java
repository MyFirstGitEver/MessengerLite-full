package com.example.messengerlite.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.ViewModelProvider;

import com.example.messengerlite.R;
import com.example.messengerlite.entities.WebLinkEntity;
import com.example.messengerlite.interfaces.AddLinkListener;
import com.example.messengerlite.viewmodels.MainViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class AddLinkSheet extends BottomSheetDialogFragment
{
    private MainViewModel mainViewModel;

    private EditText titleEditTxt, linkEditTxt;
    private AppCompatButton addBtn;
    private AddLinkListener listener;

    private String title, link;
    private Integer id;

    public AddLinkSheet addPreContext(String title, String link, Integer id)
    {
        this.title = title;
        this.link = link;
        this.id = id;

        return this;
    }

    public AddLinkSheet()
    {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.add_link_sheet, container, false);

        titleEditTxt = view.findViewById(R.id.titleEditTxt);
        linkEditTxt = view.findViewById(R.id.linkEditTxt);
        addBtn = view.findViewById(R.id.addBtn);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
        titleEditTxt.setText(title);
        linkEditTxt.setText(link);

        addBtn.setOnClickListener(v ->
        {
            WebLinkEntity link = new WebLinkEntity(
                    titleEditTxt.getText().toString(),
                    linkEditTxt.getText().toString(),
                    mainViewModel.getMyUser().getId());

            link.setId(id);

            Bundle bundle = new Bundle();
            bundle.putParcelable("link", link);
            getParentFragmentManager().setFragmentResult("addLink", bundle);

            dismiss();
        });
    }
}
