package com.agors.domain.entity;

/**
 * Сутність, що представляє історичне місце в системі.
 * <p>
 * Містить інформацію про назву, країну, еру, опис та URL зображення місця.
 * </p>
 *
 * @author agors
 * @version 1.0
 */
public class Place {

    /** Унікальний ідентифікатор місця */
    private int id;
    /** Назва місця */
    private String name;
    /** Країна, де знаходиться місце */
    private String country;
    /** Історична ера або період */
    private String era;
    /** Опис місця */
    private String description;
    /** URL зображення місця */
    private String imageUrl;

    /**
     * Повертає унікальний ідентифікатор місця.
     *
     * @return ідентифікатор місця
     */
    public int getId() {
        return id;
    }

    /**
     * Встановлює унікальний ідентифікатор місця.
     *
     * @param id ідентифікатор місця
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Повертає назву місця.
     *
     * @return назва місця
     */
    public String getName() {
        return name;
    }

    /**
     * Встановлює назву місця.
     *
     * @param name назва місця
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Повертає країну, де знаходиться місце.
     *
     * @return назва країни
     */
    public String getCountry() {
        return country;
    }

    /**
     * Встановлює країну місця.
     *
     * @param country назва країни
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Повертає історичну еру або період.
     *
     * @return назва ери
     */
    public String getEra() {
        return era;
    }

    /**
     * Встановлює історичну еру місця.
     *
     * @param era назва ери
     */
    public void setEra(String era) {
        this.era = era;
    }

    /**
     * Повертає опис місця.
     *
     * @return текстовий опис
     */
    public String getDescription() {
        return description;
    }

    /**
     * Встановлює опис місця.
     *
     * @param description текстовий опис
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Повертає URL зображення місця.
     *
     * @return URL зображення
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * Встановлює URL зображення місця.
     *
     * @param imageUrl URL зображення
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
