package com.wookes.tac.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import com.wookes.tac.R;
import com.wookes.tac.model.MentionTagModel;
import com.wookes.tac.request.WebService;
import com.wookes.tac.urlconfig.URLConfig;
import com.wookes.tac.util.CustomCounter;
import com.wookes.tac.util.GlideApp;
import com.wookes.tac.util.ToastDisplay;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MentionTagAdapter extends ArrayAdapter<MentionTagModel> {

    ArrayList<MentionTagModel> mentionTagModels = new ArrayList<>();
    private final HashMap<String, String> map = new HashMap<>();
    private final WebService webService;
    private final AutoCompleteTextView autoCompleteTextView;
    private final TextView captionCounter;

    public MentionTagAdapter(Context context, AutoCompleteTextView autoCompleteTextView, TextView captionCounter) {
        super(context, android.R.layout.simple_list_item_1);
        webService = new WebService(context);
        this.autoCompleteTextView = autoCompleteTextView;
        this.captionCounter = captionCounter;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        MentionTagModel mentionTagModel = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.inflate_layout_mention_tag, parent, false);
        }
        ImageView profilePic = convertView.findViewById(R.id.profile_pic);
        TextView hashtagPic = convertView.findViewById(R.id.hashtag_pic);
        TextView titleOne = convertView.findViewById(R.id.title_one);
        TextView titleTwo = convertView.findViewById(R.id.title_two);
        if (mentionTagModel != null && mentionTagModel.isHashtag()) {
            profilePic.setVisibility(View.GONE);
            hashtagPic.setVisibility(View.VISIBLE);
            titleOne.setText(mentionTagModel.getTitleOne());
            titleTwo.setText(getContext().getResources().getString(R.string.hashtag_total_videos, CustomCounter.format(mentionTagModel.getTitleThree())));
        } else if (mentionTagModel != null) {
            profilePic.setVisibility(View.VISIBLE);
            hashtagPic.setVisibility(View.GONE);
            GlideApp.loadSimple(getContext(), mentionTagModel.getImage(), profilePic);
            titleOne.setText(mentionTagModel.getTitleOne());
            titleTwo.setText(mentionTagModel.getTitleTwo());
        }
        return convertView;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return myFilter;
    }

    Filter myFilter = new Filter() {
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            MentionTagModel mentionTagModel = (MentionTagModel) resultValue;
            StringBuilder stringBuffer = new StringBuilder(autoCompleteTextView.getText().toString());
            Spannable textSpan = autoCompleteTextView.getText();
            final int selection = autoCompleteTextView.getSelectionStart();
            final Pattern pattern = Pattern.compile("([@#]\\w+)");
            final Matcher matcher = pattern.matcher(textSpan);
            int start;
            int end;
            while (matcher.find()) {
                start = matcher.start();
                end = matcher.end();
                if (start <= selection && selection <= end) {
                    stringBuffer.replace(start + 1, end, mentionTagModel.getTitleOne() + " ");
                    break;
                }
            }
            return stringBuffer;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null && constraint.toString().length() > 0 && captionCounter != null) {
                captionCounter.setText(getContext().getResources().getString(R.string.caption_counter, constraint.toString().length()));
            } else if (captionCounter != null) {
                captionCounter.setText("");
            }
            String findSuggestions = getCurrentWord();
            if (!TextUtils.isEmpty(findSuggestions)) {
                map.put("query", findSuggestions);
                mentionTagModels.clear();
                mentionTagModels.addAll(getSuggestions());
                FilterResults filterResults = new FilterResults();
                filterResults.values = mentionTagModels;
                filterResults.count = mentionTagModels.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            ArrayList<MentionTagModel> mentionTagModels = (ArrayList<MentionTagModel>) results.values;
            if (results.count > 0) {
                clear();
                for (MentionTagModel mentionTagModel : mentionTagModels) {
                    add(mentionTagModel);
                    notifyDataSetChanged();
                }
            } else {
                notifyDataSetInvalidated();
            }
        }
    };

    private ArrayList<MentionTagModel> getSuggestions() {
        ArrayList<MentionTagModel> mentionTagModels = new ArrayList<>();
        String result = webService.sendPost(URLConfig.MENTION_TAG_SUGGESTIONS, map);
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (!jsonObject.has("success")) {
                ToastDisplay.a(getContext(), jsonObject.getString("error"), 0);
                return mentionTagModels;
            }
            Type type = new TypeToken<ArrayList<MentionTagModel>>() {
            }.getType();
            mentionTagModels.addAll(new Gson().fromJson(jsonObject.getString("data"), type));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mentionTagModels;
    }

    public String getCurrentWord() {
        Spannable textSpan = autoCompleteTextView.getText();
        final int selection = autoCompleteTextView.getSelectionStart();
        final Pattern pattern = Pattern.compile("([@#]\\w+)");
        final Matcher matcher = pattern.matcher(textSpan);
        int start = 0;
        int end = 0;
        String currentWord = "";
        while (matcher.find()) {
            start = matcher.start();
            end = matcher.end();
            if (start <= selection && selection <= end) {
                currentWord = textSpan.subSequence(start, end).toString();
                break;
            }
        }
        textSpan.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
            }

            @Override
            public void updateDrawState(@NonNull TextPaint textPaint) {
                textPaint.setTypeface(Typeface.create(ResourcesCompat.getFont(getContext(), R.font.proxima_nova_reg), Typeface.BOLD));
                textPaint.setUnderlineText(false);
            }
        }, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return currentWord;
    }


}