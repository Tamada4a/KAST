package com.example.kast.controllers.dto.match;


import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Класс содержит всю информацию об игроке, получаемую во время матча с сервера
 *
 * @author Кирилл "Tamada" Симовин
 */
@Data
@NoArgsConstructor
public class MatchPlayerDTO {
    /**
     * Текущая команда игрока: CT, T, Spectator (Unassigned)
     */
    private String team;

    /**
     * Никнейм игрока
     */
    private String nick;

    /**
     * Есть ли у игрока набор сапера
     */
    private boolean defuseKit;

    /**
     * Пистолет игрока
     */
    private String pistol;

    /**
     * Лучшее оружие игрока.
     * Если у игрока в инвентаре есть и пистолет, и оружие другого типа, лучшим считается оружие другого типа.
     * В случае, если у игрока только пистолет, лучшим оружием считается его пистолет.<br/>
     * Переменная может быть пустой, если у игрока в инвентаре только нож
     */
    private String weapon;

    /**
     * Количество очков здоровья игрока
     */
    private int hp;

    /**
     * Наличие брони у игрока:
     * <li><b>0</b> - отсутствие</li>
     * <li><b>1</b> - броня</li>
     * <li><b>2</b> - шлем + броня</li>
     */
    private int armor;

    /**
     * Количество денег игрока
     */
    private int money;

    /**
     * Количество килов игрока
     */
    private int kills;

    /**
     * Количество килов в голову.
     * Используется только для дальнейшего занесения в общую статистику игрока, наблюдаемую на его странице
     */
    private int hsKills;

    /**
     * Количество смертей игрока
     */
    private int deaths;

    /**
     * Количество ассистов игрока
     */
    private int assists; //

    /**
     * Общее количество нанесённого урона игроком.
     * Используется для вычисления среднего урона игрока за карту
     */
    private int fullDamage;


    /**
     * Задаём игроку параметры как для первого раунда ММ
     *
     * @param nick никнейм игрока на сервере
     */
    public MatchPlayerDTO(String nick) {
        this.team = "";
        this.nick = nick;
        this.defuseKit = false;
        this.weapon = "";
        this.pistol = "";
        this.hp = 100;
        this.armor = 0;
        this.money = 800;
        this.kills = 0;
        this.hsKills = 0;
        this.deaths = 0;
        this.assists = 0;
        this.fullDamage = 0;
    }
}
