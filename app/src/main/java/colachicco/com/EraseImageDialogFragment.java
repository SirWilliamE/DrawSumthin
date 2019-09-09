// allows the user to erase the image
package colachicco.com;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class EraseImageDialogFragment extends DialogFragment {
    // create an AlertDialog and return it
    @Override
    public Dialog onCreateDialog(Bundle bundle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // set the AlertDialog message
        builder.setMessage(R.string.message_erase);

        // add Erase Button
        builder.setPositiveButton(R.string.button_erase,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        getDoodleFragment().getDoodleView().clear(); // clear image
                    }
                });

        // add cancel button
        builder.setNegativeButton(android.R.string.cancel, null);
        return builder.create(); // return dialog
    }

}
