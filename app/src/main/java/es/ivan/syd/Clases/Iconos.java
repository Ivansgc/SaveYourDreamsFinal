package es.ivan.syd.Clases;

import es.ivan.syd.R;

/**
 * Created by Iván on 01/12/2016.
 */

public enum Iconos{

    NEUTRA(0, R.drawable.ic_icon_neutro),
    BIEN(1, R.drawable.ic_icon_carabien),
    MAL(2, R.drawable.ic_icon_caramal);

    private int drawableId;
    private int id;

    Iconos(int id, int drawableId){
        this.id = id;
        this.drawableId = drawableId;
    }

    public int getDrawableId() {
        return drawableId;
    }

    public int getId() {
        return id;
    }

    public static Iconos fromId(int id){
        switch (id){
            case 0:
                return NEUTRA;
            case 1:
                return BIEN;
            case 2:
                return MAL;
        }
        return NEUTRA;
    }
}
