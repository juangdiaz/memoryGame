package com.sho.hire.hw.DiazJuanMemory;

/**
 * @author juandiaz <juandiaz@us.univision.com> Android Developer
 *         Copyright (C) 2016, Univision Communications Inc.
 *
 *         Model Required to retrive the Images from Flickr, and Image URL Creation
 */
class Image {

    private Long id;
    private String secret;
    private String server;
    private String farm;
    int position;
    private String imgUrl;

    Image(Long id, String secret, String server, String farm) {
        super();
        this.id = id;
        this.secret = secret;
        this.server = server;
        this.farm = farm;
        setImgUrl(createPhotoURL(this));
    }

    String getImgUrl() {
        return imgUrl;
    }

    private void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    Long getId() {
        return id;
    }

    private String createPhotoURL( Image imgCon) {
        String tmp = null;
        tmp = "http://farm" + imgCon.farm + ".staticflickr.com/" + imgCon.server + "/" + imgCon.id + "_" + imgCon.secret + "_s.jpg";
        return tmp;
    }
}
