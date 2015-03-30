package nl.tue.the30daychallenge;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.ShareOpenGraphAction;
import com.facebook.share.model.ShareOpenGraphContent;
import com.facebook.share.model.ShareOpenGraphObject;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.widget.ShareDialog;

/**
 * Created by kevin on 3/27/15.
 */
public class Share implements DialogInterface.OnClickListener {

    Activity parent;
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static Bitmap image = null;
    public static enum ACTION {
        COMPLETE, FAIL, DO
    };
    public static ACTION action;
    public String challengeTitle;

    public void share(Activity parent, ACTION action, String challengeTitle) {
        this.parent = parent;
        this.action = action;
        this.challengeTitle = challengeTitle;
        AlertDialog.Builder builder = new AlertDialog.Builder(parent);
        builder
                .setMessage("Do you want to add a selfie?")
                .setPositiveButton("Yes", this)
                .setNegativeButton("No", this)
                .setNeutralButton("Cancel", this)
        .show();
    }

    public void shareOnFacebook() {

        // this part is optional
        MainActivity.shareDialog.registerCallback(MainActivity.callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Log.d("Share", "Callback success");
            }

            @Override
            public void onCancel() {
                Log.d("Share", "Callback cancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("Share", "Callback error: " + error.toString());
            }
        });

        if (ShareDialog.canShow(ShareLinkContent.class)) {

            // action = complete, fail, do
            String strAction = "complete";
            if (action.equals(ACTION.COMPLETE)) strAction = "complete";
            if (action.equals(ACTION.DO)) strAction = "do";
            if (action.equals(ACTION.FAIL)) strAction = "fail";
            Log.d("Share", "Found action: " + strAction);
            Log.d("Share", "Challenge title: " + challengeTitle);

            ShareOpenGraphObject.Builder objectBuilder = new ShareOpenGraphObject.Builder();
            objectBuilder
                .putString("og:type", "the-challenge-app:challenge")
                .putString("og:title", "The30DayChallenge app")
                .putString("og:description", challengeTitle)
            ;
            if (image != null) {
                SharePhoto photo = new SharePhoto.Builder()
                    .setBitmap(image)
                    .build();
                objectBuilder.putPhoto("og:image", photo);
            }
            ShareOpenGraphObject object = objectBuilder.build();
            ShareOpenGraphAction action = new ShareOpenGraphAction.Builder()
                .setActionType("the-challenge-app:" + strAction)
                .putObject("the-challenge-app:challenge", object)
                .build();
            ShareOpenGraphContent content = new ShareOpenGraphContent.Builder()
                .setPreviewPropertyName("the-challenge-app:challenge")
                .setAction(action)
                .build();

            MainActivity.shareDialog.show(content);
        }
    }

    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == parent.RESULT_OK) {
            Bundle extras = data.getExtras();
            this.image = (Bitmap) extras.get("data");
            Log.d("Share", "Image: " + this.image);
            shareOnFacebook();
        } else {
            MainActivity.callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_NEGATIVE:
                // the user does not want to add a selfie
                this.image = null;
                shareOnFacebook();
                break;
            case DialogInterface.BUTTON_POSITIVE:
                // the user wants to add a selfie
                this.image = null;
                new Intent(parent, Share.class);
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(parent.getPackageManager()) != null) {
                    parent.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
                break;
            case DialogInterface.BUTTON_NEUTRAL:
                // action completed, nothing done
                break;
        }
    }

}
