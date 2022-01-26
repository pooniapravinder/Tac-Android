package com.wookes.tac.ui.g;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import com.wookes.tac.R;
import com.wookes.tac.ui.Hashtag;
import com.wookes.tac.ui.UserProfile;
import com.wookes.tac.util.TimeUtils;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NotificationText {
    private static final String TAG_PATTERN = ".*?(@\\w+).*?";
    private static final String TEXT_TAG_PATTERN = ".*?(@|#\\w+).*?";
    private Typeface typeface;
    private final Context context;

    public NotificationText(Context context) {
        this.context = context;
    }

    public String getNotificationTime(long timestamp) {
        Calendar nowTime = Calendar.getInstance();
        Calendar neededTime = Calendar.getInstance();
        neededTime.setTimeInMillis(timestamp);
        if (nowTime.get(Calendar.DATE) == neededTime.get(Calendar.DATE)) {
            return "Today";
        } else if (nowTime.get(Calendar.DATE) - neededTime.get(Calendar.DATE) == 1) {
            return "Yesterday";
        } else if (nowTime.get(Calendar.WEEK_OF_YEAR) == neededTime.get(Calendar.WEEK_OF_YEAR) && nowTime.get(Calendar.YEAR) == neededTime.get(Calendar.YEAR)) {
            return "This Week";
        } else if (neededTime.get(Calendar.MONTH) == nowTime.get(Calendar.MONTH) && nowTime.get(Calendar.YEAR) == neededTime.get(Calendar.YEAR)) {
            return "This Month";
        } else {
            return "Earlier";
        }
    }

    public void setNotificationText(TextView textView, String[] users, String text, byte type, long timestamp) {
        textView.setText(TextUtils.concat(getTags(getUsers(users)), getBoldText(users), " " + NotificationHelper.getNotificationText(type), text != null ? ": " + getTextTags(text) : "", " " + TimeUtils.getTimeAgo(timestamp)));
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private SpannableString getTags(String pTagString) {
        typeface = ResourcesCompat.getFont(context, R.font.proxima_nova_reg);
        Pattern tagMatcher = Pattern.compile(TAG_PATTERN);
        SpannableStringBuilder string = new SpannableStringBuilder(pTagString);
        int start = -1;
        for (int i = 0; i < pTagString.length(); i++) {
            if ((pTagString.charAt(i) == '@') && (start == -1)) {
                start = i;
            } else if (!String.valueOf(pTagString.charAt(i)).matches("\\w") || (i == pTagString.length() - 1 && start != -1)) {
                char currentChar = pTagString.charAt(i);
                int currentPos = i;
                if (start != -1) {
                    if (i == pTagString.length() - 1) {
                        i++;
                    }
                    final String tag = pTagString.substring(start, i);
                    Matcher m = tagMatcher.matcher(tag.toLowerCase());
                    if (m.find()) {
                        string.setSpan(new ClickableSpan() {
                            @Override
                            public void onClick(@NonNull View widget) {
                                Intent intent = new Intent(context, UserProfile.class);
                                intent.putExtra("user", tag.replace("@", ""));
                                intent.putExtra("user_type", 1);
                                context.startActivity(intent);
                            }

                            @Override
                            public void updateDrawState(@NonNull TextPaint textPaint) {
                                textPaint.setTypeface(Typeface.create(typeface, Typeface.BOLD));
                                textPaint.setColor(Color.BLACK);
                                textPaint.setUnderlineText(false);
                            }
                        }, start, i, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        pTagString = removeCharAt(pTagString, start);
                        string.delete(start, start + 1);
                        i = i - 1;
                    }
                    start = (currentChar == '@') ? currentPos : -1;
                }
            }
        }
        return SpannableString.valueOf(string);
    }

    private static String removeCharAt(String s, int pos) {
        return s.substring(0, pos) + s.substring(pos + 1);
    }

    private String getUsers(String[] users) {
        String text;
        switch (users.length) {
            case 1:
                text = users[0];
                break;
            case 2:
                text = users[0] + " and " + users[1];
                break;
            case 3:
                text = users[0] + " , " + users[1] + " and " + users[2];
                break;
            default:
                text = users[0] + " , " + users[1] + " and";
                break;
        }
        return text;
    }

    private SpannableString getTextTags(String pTagString) {
        typeface = ResourcesCompat.getFont(context, R.font.proxima_nova_reg);
        Pattern tagMatcher = Pattern.compile(TEXT_TAG_PATTERN);
        SpannableString string = new SpannableString(pTagString);
        int start = -1;
        for (int i = 0; i < pTagString.length(); i++) {
            if ((pTagString.charAt(i) == '@' || pTagString.charAt(i) == '#') && (start == -1)) {
                start = i;
            } else if (!String.valueOf(pTagString.charAt(i)).matches("\\w") || (i == pTagString.length() - 1 && start != -1)) {
                char currentChar = pTagString.charAt(i);
                int currentPos = i;
                if (start != -1) {
                    if (i == pTagString.length() - 1) {
                        i++;
                    }
                    final String tag = pTagString.substring(start, i);
                    Matcher m = tagMatcher.matcher(tag.toLowerCase());
                    if (m.find()) {
                        string.setSpan(new ClickableSpan() {
                            @Override
                            public void onClick(@NonNull View widget) {
                                Intent intent = new Intent(context, tag.charAt(0) == '@' ? UserProfile.class : Hashtag.class);
                                if (tag.charAt(0) == '@') {
                                    intent.putExtra("user", tag.replace("@", ""));
                                    intent.putExtra("user_type", 1);
                                } else {
                                    intent.putExtra("hashtag", tag.replace("#", ""));
                                    intent.putExtra("hashtag_type", 1);
                                }
                                context.startActivity(intent);
                            }

                            @Override
                            public void updateDrawState(@NonNull TextPaint textPaint) {
                                textPaint.setTypeface(Typeface.create(typeface, Typeface.BOLD));
                                textPaint.setUnderlineText(false);
                            }
                        }, start, i, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    start = (currentChar == '@' || currentChar == '#') ? currentPos : -1;
                }
            }
        }
        return string;
    }

    private SpannableString getBoldText(String[] users) {
        String text = "";
        if (users.length > 3) text = " " + (users.length - 2) + " others";
        SpannableStringBuilder string = new SpannableStringBuilder(text);
        StyleSpan styleSpan = new StyleSpan(android.graphics.Typeface.BOLD);
        ForegroundColorSpan foregroundSpan = new ForegroundColorSpan(Color.BLACK);
        string.setSpan(styleSpan, 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        string.setSpan(foregroundSpan, 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return SpannableString.valueOf(string);
    }

}
