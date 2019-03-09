package dev.tetrabyte.whitelistverification;

public class Data {
    private String expiresIn;
    private String expires;
    private Integer code;

    public String getExpires () {
        return expires;
    }

    public void setExpires (String expires) {
        this.expires = expires;
    }

    public String getExpiresIn () {
        return expiresIn;
    }

    public void setExpiresIn (String expiresIn) {
        this.expiresIn = expiresIn;
    }

    public Integer getCode () {
        return code;
    }

    public void setCode (Integer code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "ClassPojo [expires = "+expires+", code = "+code+", expiresIn = "+expiresIn+"]";
    }
}