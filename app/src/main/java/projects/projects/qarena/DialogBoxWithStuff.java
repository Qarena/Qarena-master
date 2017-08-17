package projects.projects.qarena;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;

/**
 * Created by Arka Bhowmik on 8/15/2017.
 */

//FOR VIEWING THE PRIZES IN THE QUIZ SECTION
public class DialogBoxWithStuff extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.dialog_prizes, null))
                // Add action buttons
                .setPositiveButton("REGISTER", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // sign in the user ...
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                       DialogBoxWithStuff.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }
}