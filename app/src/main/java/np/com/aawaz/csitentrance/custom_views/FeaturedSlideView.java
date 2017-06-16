package np.com.aawaz.csitentrance.custom_views;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.objects.Feedback;

public class FeaturedSlideView extends BaseSliderView {
    private final Context context;

    public FeaturedSlideView(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public View getView() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.featured_slide_view, null);

        ImageView target = (ImageView) v.findViewById(R.id.imageViewFeatured);
        TextView description = (TextView) v.findViewById(R.id.detailFeatured);
        TextView title = (TextView) v.findViewById(R.id.titleFeatured);
        TextView address = (TextView) v.findViewById(R.id.addressFeatured);
        TextView knowMore = (TextView) v.findViewById(R.id.knowMoreFeatured);
        TextView call = (TextView) v.findViewById(R.id.callFeatured);

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference()
                        .child("ad_user_data")
                        .child(getDescription())
                        .child("students")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .setValue(new Feedback(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()));
                context.startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", String.valueOf(getBundle().getLong("phone_no")), null)));
            }
        });
        knowMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference()
                        .child("ad_user_data")
                        .child(getDescription())
                        .child("students")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .setValue(new Feedback(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()));
                context.startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", String.valueOf(getBundle().getLong("phone_no")), null)));

            }
        });

        title.setText(getDescription());
        description.setText(getBundle().getString("description"));
        address.setText(getBundle().getString("address"));
        bindEventAndShow(v, target);
        return v;
    }
}
