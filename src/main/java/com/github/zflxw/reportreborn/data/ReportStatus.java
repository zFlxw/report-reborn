package com.github.zflxw.reportreborn.data;

import com.github.zflxw.reportreborn.ReportReborn;
import com.github.zflxw.reportreborn.localization.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum ReportStatus {
    OPEN("report_status.open"),
    REVIEWING("report_status.reviewing"),
    ACCEPTED("report_status.accepted"),
    DENIED("report_status.denied"),
    CLOSED("report_status.closed");

    @NotNull
    private final String languageKey;

    ReportStatus(@NotNull String languageKey) {
        this.languageKey = languageKey;
    }

    @Nullable
    public String getString(Language language) {
        return ReportReborn.getInstance().getTranslator().get(language, languageKey);
    }

    @NotNull
    public String getLanguageKey() {
        return this.languageKey;
    }
}
