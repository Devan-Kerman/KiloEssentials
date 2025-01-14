package org.kilocraft.essentials.util;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import net.minecraft.network.chat.Component;
import net.minecraft.server.players.StoredUserEntry;

public abstract class MuteEntry<T> extends StoredUserEntry<T> {
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
    protected final Date creationDate;
    protected final String source;
    protected final Date expires;
    protected final String reason;

    public MuteEntry(T object, @Nullable Date creationDate, @Nullable String source, @Nullable Date expiryDate, @Nullable String reason) {
        super(object);
        this.creationDate = creationDate;
        this.source = source;
        this.expires = expiryDate;
        this.reason = reason == null ? "Muted by an operator." : reason;
    }

    protected MuteEntry(T object, JsonObject jsonObject) {
        super(object);
        Date created;
        try {
            created = jsonObject.has("created") ? DATE_FORMAT.parse(jsonObject.get("created").getAsString()) : new Date();
        } catch (ParseException e) {
            created = new Date();
        }

        Date expiry;
        try {
            expiry = jsonObject.has("expires") ? DATE_FORMAT.parse(jsonObject.get("expires").getAsString()) : new Date();
        } catch (ParseException e) {
            expiry = new Date();
        }

        this.expires = expiry;
        this.creationDate = created;
        this.source = jsonObject.has("source") ? jsonObject.get("source").getAsString() : "(Unknown)";
        this.reason = jsonObject.has("reason") ? jsonObject.get("reason").getAsString() : null;
    }

    public String getSource() {
        return this.source;
    }

    public Date getExpires() {
        return this.expires;
    }

    public String getReason() {
        return this.reason;
    }

    public abstract Component toText();

    public boolean hasExpired() {
        System.out.println("hasExpired: " + this.expires);
        if (this.expires == null) {
            return false;
        }
        System.out.println("hasExpired2: " + this.expires.before(new Date()));
        return this.expires.before(new Date());
    }

    @Override
    protected void serialize(JsonObject jsonObject) {
        jsonObject.addProperty("created", DATE_FORMAT.format(this.creationDate));
        jsonObject.addProperty("source", this.source);
        jsonObject.addProperty("expires", this.expires == null ? "forever" : DATE_FORMAT.format(this.expires));
        jsonObject.addProperty("reason", this.reason);
    }
}
