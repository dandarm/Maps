package com.example.romamappe.romamappe;

/**
 * Created by Utente on 09/12/2016.
 */
public class TipoScelto {

    public enum OggettoPubblico {
        Generici,
        Scuole,
        Cassonetti,
        Lampioni,
        Tombini
    }

    static public int qualeImmagine(TipoScelto.OggettoPubblico tipo) {
        int id=0;
        switch (tipo) {
            case Generici:
                id = R.drawable.mapmarker48;
                break;
            case Scuole:
                id = R.drawable.home_png48;
                break;
            case Cassonetti:
                id = R.drawable.trashcan;
                break;
            case Lampioni:
                id = R.drawable.lampione48;
                break;
            case Tombini:
                id = R.drawable.tombino48;
                break;
            default:
                id = R.drawable.mapmarker48;
                break;
        }
        return id;
    }
}
