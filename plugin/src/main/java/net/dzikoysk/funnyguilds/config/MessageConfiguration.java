package net.dzikoysk.funnyguilds.config;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.exception.OkaeriException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils;

public class MessageConfiguration extends OkaeriConfig {

    @Comment("<------- Global Date Format -------> #")
    public SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    @Comment("<------- No Value Messages -------> #")
    public String gNameNoValue = "Brak (G-NAME/NAME)";
    public String gTagNoValue = "Brak (G-TAG/TAG)";
    public String gOwnerNoValue = "Brak (G-OWNER)";
    public String gDeputiesNoValue = "Brak (G-DEPUTIES)";
    public String gDeputyNoValue = "Brak (G-DEPUTY)";
    public String gValidityNoValue = "Brak (G-VALIDITY)";
    public String gRegionSizeNoValue = "Brak (G-REGION-SIZE)";
    public String alliesNoValue = "Brak (ALLIES)";
    public String enemiesNoValue = "Brak (ENEMIES)";
    public String gtopNoValue = "Brak (GTOP-x)";
    public String ptopNoValue = "Brak (PTOP-x)";
    public String wgRegionNoValue = "Brak (WG-REGION)";
    public String minMembersToIncludeNoValue = "Brak (guild-min-members w config.yml)";

    @Comment("<------- Permission Messages -------> #")
    public String permission = "&cNie masz wystarczajacych uprawnien do uzycia tej komendy!";
    public String blockedWorld = "&cZarzadzanie gildiami jest zablokowane na tym swiecie!";
    public String playerOnly = "&cKomenda dostepna tylko dla graczy!";

    @Comment("<------- Rank Messages -------> #")
    public String rankLastVictimV = "&7Ostatnio zostales zabity przez tego samego gracza, punkty nie zostaja odebrane!";
    public String rankLastVictimA = "&7Ostatnio zabiles tego samego gracza, punkty nie zostaja dodane!";
    public String rankLastAttackerV = "&7Ostatnio zostales zabity przez tego samego gracza, punkty nie zostaja odebrane!";
    public String rankLastAttackerA = "&7Ten gracz byl ostatnio zabity przez Ciebie, punkty nie zostaja dodane!";
    public String rankIPVictim = "&7Ten gracz ma taki sam adres IP, punkty nie zostaja odjete!";
    public String rankIPAttacker = "&7Ten gracz ma taki sam adres IP, punkty nie zostaja dodane!";
    @Comment("Dostepne zmienne: {ATTACKER}, {VICTIM}, {-}, {+}, {POINTS}, {POINTS-FORMAT}, {VTAG}, {ATAG}, {WEAPON}, {WEAPON-NAME}, {REMAINING-HEALTH}, {REMAINING-HEARTS}, {ASSISTS}")
    public String rankDeathMessage = "{ATAG}&b{ATTACKER} &7(&a+{+}&7) zabil {VTAG}&b{VICTIM} &7(&c-{-}&7) uzywajac &b{WEAPON} {WEAPON-NAME}";
    public String rankKillTitle = "&cZabiles gracza {VICTIM}";
    public String rankKillSubtitle = "&7+{+}";
    @Comment("Zamiast zmiennej {ASSISTS} wstawiane sa kolejne wpisy o asystujacych graczach")
    public String rankAssistMessage = "&7Asystowali: {ASSISTS}";
    @Comment("Dostepne zmienne: {PLAYER}, {+}, {SHARE}")
    public String rankAssistEntry = "&b{PLAYER} &7(&a+{+}&7, {SHARE}% dmg)";
    @Comment("Znaki oddzielajace kolejne wpisy o asystujacych graczach")
    public String rankAssistDelimiter = "&8, ";
    @Comment("Dostepne zmienne: {LAST-RANK}, {CURRENT-RANK}")
    public String rankResetMessage = "&7Zresetowales swoj ranking z poziomu &c{LAST-RANK} &7do poziomu &c{CURRENT-RANK}&7.";

    @Comment("<------- Ban Messages -------> #")
    @Comment("Dostepne zmienne: {PLAYER}, {REASON}, {DATE}, {NEWLINE}")
    public String banMessage = "&7Zostales zbanowany do &b{DATE}{NEWLINE}{NEWLINE}&7za: &b{REASON}";

    @Comment("<------- Region Messages -------> #")
    public String regionOther = "&cTen teren nalezy do innej gildii!";
    public String regionCenter = "&cNie mozesz zniszczyc srodka swojej gildii!";
    @Comment("Dostepne zmienne: {TIME}")
    public String regionExplode = "&cBudowanie na terenie gildii zablokowane na czas &4{TIME} sekund&c!";
    @Comment("Dostepne zmienne: {TIME}")
    public String regionExplodeInteract = "&cNie mozna budowac jeszcze przez &4{TIME} sekund&c!";
    public String regionCommand = "&cTej komendy nie mozna uzyc na terenie innej gildii!";
    public String regionExplosionHasProtection = "&cEksplozja nie spowodowala zniszczen na terenie gildii, poniewaz jest ona chroniona!";
    public String regionsDisabled = "&cRegiony gildii sa wylaczone!";

    @Comment("<------- ActionBar Region Messages -------> #")
    @Comment("Dostepne zmienne: {PLAYER}")
    public String notificationActionbarIntruderEnterGuildRegion = "&7Gracz &c{PLAYER} &7wkroczyl na teren &cTwojej &7gildii!";
    @Comment("Dostepne zmienne: {GUILD}, {TAG}")
    public String notificationActionbarEnterGuildRegion = "&7Wkroczyles na teren gildii &c{TAG}&7!";
    @Comment("Dostepne zmienne: {GUILD}, {TAG}")
    public String notificationActionbarLeaveGuildRegion = "&7Opusciles teren gildii &c{TAG}&7!";

    @Comment("<------- Bossbar Region Messages -------> #")
    @Comment("Dostepne zmienne: {PLAYER}")
    public String notificationBossbarIntruderEnterGuildRegion = notificationActionbarIntruderEnterGuildRegion;
    @Comment("Dostepne zmienne: {GUILD}, {TAG}")
    public String notificationBossbarEnterGuildRegion = notificationActionbarEnterGuildRegion;
    @Comment("Dostepne zmienne: {GUILD}, {TAG}")
    public String notificationBossbarLeaveGuildRegion = notificationActionbarLeaveGuildRegion;

    @Comment("<------- Chat Region Messages -------> #")
    @Comment("Dostepne zmienne: {PLAYER}")
    public String notificationChatIntruderEnterGuildRegion = notificationActionbarIntruderEnterGuildRegion;
    @Comment("Dostepne zmienne: {GUILD}, {TAG}")
    public String notificationChatEnterGuildRegion = notificationActionbarEnterGuildRegion;
    @Comment("Dostepne zmienne: {GUILD}, {TAG}")
    public String notificationChatLeaveGuildRegion = notificationActionbarLeaveGuildRegion;

    @Comment("<------- Title Region Messages -------> #")
    @Comment("Dostepne zmienne: {PLAYER}")
    public String notificationTitleIntruderEnterGuildRegion = notificationActionbarIntruderEnterGuildRegion;
    @Comment("Dostepne zmienne: {PLAYER}")
    public String notificationSubtitleIntruderEnterGuildRegion = notificationActionbarIntruderEnterGuildRegion;
    @Comment("Dostepne zmienne: {GUILD}, {TAG}")
    public String notificationTitleEnterGuildRegion = notificationActionbarEnterGuildRegion;
    @Comment("Dostepne zmienne: {GUILD}, {TAG}")
    public String notificationSubtitleEnterGuildRegion = notificationActionbarEnterGuildRegion;
    @Comment("Dostepne zmienne: {GUILD}, {TAG}")
    public String notificationTitleLeaveGuildRegion = notificationActionbarLeaveGuildRegion;
    @Comment("Dostepne zmienne: {GUILD}, {TAG}")
    public String notificationSubtitleLeaveGuildRegion = notificationActionbarLeaveGuildRegion;

    @Comment("<------- Broadcast Messages -------> #")
    @Comment("Dostepne zmienne: {PLAYER}, {GUILD}, {TAG}")
    public String broadcastCreate = "&a{PLAYER} &7zalozyl gildie o nazwie &a{GUILD} &7i tagu &a{TAG}&7!";
    @Comment("Dostepne zmienne: {PLAYER}, {GUILD}, {TAG}")
    public String broadcastDelete = "&c{PLAYER} &7rozwiazal gildie &c{TAG}&7!";
    @Comment("Dostepne zmienne: {PLAYER}, {GUILD}, {TAG}")
    public String broadcastJoin = "&a{PLAYER} &7dolaczyl do gildii &a{TAG}&7!";
    @Comment("Dostepne zmienne: {PLAYER}, {GUILD}, {TAG}")
    public String broadcastLeave = "&c{PLAYER} &7opuscil gildie &c{TAG}&7!";
    @Comment("Dostepne zmienne: {PLAYER}, {GUILD}, {TAG}")
    public String broadcastKick = "&c{PLAYER} &7zostal &cwyrzucony &7z gildii &c{TAG}&7!";
    @Comment("Dostepne zmienne: {GUILD}, {TAG}, {REASON}, {TIME}")
    public String broadcastBan = "&7Gildia &c{TAG}&7 zostala zbanowana za &c{REASON}&7, gratulacje!";
    @Comment("Dostepne zmienne: {GUILD}, {TAG}")
    public String broadcastUnban = "&7Gildia &a{TAG}&7 zostala &aodbanowana&7!";
    @Comment("Dostepne zmienne: {GUILD}, {TAG}, {X}, {Y}, {Z}")
    public String broadcastValidity = "&7Gildia &b{TAG} &7wygasla&b! &7Jej baza znajdowala sie na x: &b{X} &7y: &b{Y} &7z: &b{Z}&7!";
    @Comment("Dostepne zmienne: {WINNER}, {LOSER}")
    public String broadcastWar = "&7Gildia &4{WINNER}&7 podblila gildie &4{LOSER}&7!!";

    @Comment("<------- Help Messages -------> #")
    public List<String> helpList = Arrays.asList(
            "&7---------------------&8[ &aGildie &8]&7---------------------",
            "&a/zaloz [tag] [nazwa] &8- &7Tworzy gildie",
            "&a/zapros [gracz] &8- &7Zaprasza gracza do gildii",
            "&a/dolacz [tag] &8- &7Przyjmuje zaproszenie do gildii",
            "&a/info [tag] &8- &7Informacje o danej gildii",
            "&a/baza &8- &7Teleportuje do bazy gildii",
            "&a/powieksz &8- &7Powieksza teren gildii",
            "&a/przedluz &8- &7Przedluza waznosc gildii",
            "&a/lider [gracz] &8- &7Oddaje zalozyciela gildii",
            "&a/zastepca [gracz] &8- &7Nadaje zastepce gildii",
            "&a/sojusz [tag] &8- &7Pozwala nawiazac sojusz",
            "&a/opusc &8- &7Opuszcza gildie",
            "&a/wyrzuc [gracz] &8- &7Wyrzuca gracza z gildii",
            "&a/rozwiaz [tag] &8- &7Rozwiazuje sojusz",
            "&a/usun &8- &7Usuwa gildie",
            "&a/przedmioty &8- &7Pokazuje przedmioty potrzebne do zalozenia gildii",
            "&a/ucieczka &8- &7Rozpoczyna ucieczke z terenu innej gildii");

    @Comment("<------- Admin Help Messages -------> #")
    public List<String> adminHelpList = Arrays.asList(
            "&a/ga dodaj [tag] [nick] &8- &7Dodaje gracza do gildii",
            "&a/ga usun [tag] &8- &7Usuwa gildie",
            "&a/ga wyrzuc [nick] &8- &7Wyrzuca gracza z gildii",
            "&a/ga tp [tag] &8- &7Teleportuje do bazy gildii",
            "&a/ga points [nick] [points] &8- &7Ustawia liczbe punktow gracza",
            "&a/ga kills [nick] [kills] &8- &7Ustawia liczbe zabojstw gracza",
            "&a/ga deaths [nick] [deaths] &8- &7Ustawia liczbe smierci gracza",
            "&a/ga ban [tag] [czas] [powod] &8- &7Banuje gildie na okreslony czas",
            "&a/ga unban [tag] &8- &7Odbanowywuje gildie",
            "&a/ga zycia [tag] [zycia] &8- &7Ustawia liczbe zyc gildii",
            "&a/ga przenies [tag] &8- &7Przenosi teren gildii",
            "&a/ga przedluz [tag] [czas] &8- &7Przedluza waznosc gildii o podany czas",
            "&a/ga ochrona [tag] [czas] &8- &7Ustawia date wygasniecia ochrony",
            "&a/ga nazwa [tag] [nazwa] &8- &7Zmienia nazwe gildii",
            "&a/ga tag [tag] [nowy tag] &8- &7Zmienia tag gildii",
            "&a/ga spy &8- &7Szpieguje czat gildii",
            "&a/ga enabled &8- &7Zarzadzanie statusem zakladania gildii",
            "&a/ga lider [tag] [gracz] &8- &7Zmienia lidera gildii",
            "&a/ga zastepca [tag] [gracz] &8- &7Nadaje zastepce gildii",
            "&a/ga baza [gracz] &8- &7Teleportuje gracza do bazy jego gildii");

    @Comment("Dostepne zmienne: {PLAYER}, {GUILD}, {TAG}, {POINTS}, {POINTS-FORMAT}, {KILLS}, {DEATHS}, {ASSISTS}, {LOGOUTS}, {KDR}, {RANK}")
    public List<String> playerInfoList = Arrays.asList(
            "&8--------------.-----------------",
            "&7Gracz: &a{PLAYER}",
            "&7Gildia: &a{TAG}",
            "&7Miejsce: &a{RANK} &8(&a{POINTS}&8)",
            "&7Zabojstwa: &a{KILLS}",
            "&7Smierci: &a{DEATHS}",
            "&7Asysty: &a{ASSISTS}",
            "&7Logouty: &a{LOGOUTS}",
            "&7KDR: &a{KDR}",
            "&8-------------.------------------");

    @Comment("Dostepne zmienne: {PLAYER}, {GUILD}, {TAG}, {POINTS}, {POINTS-FORMAT}, {KILLS}, {DEATHS}, {ASSISTS}, {LOGOUTS}, {KDR}, {RANK}")
    public List<String> playerRightClickInfo = Arrays.asList(
            "&8--------------.-----------------",
            "&7Gracz: &a{PLAYER}",
            "&7Gildia: &a{TAG}",
            "&7Miejsce: &a{RANK} &8(&a{POINTS}&8)",
            "&8-------------.------------------");

    @Comment("<------- Info Messages -------> #")
    public String infoTag = "&cPodaj tag gildii!";
    public String infoExists = "&cGildia o takim tagu nie istnieje!";

    @Comment("Dostepne zmienne: {GUILD}, {TAG}, {OWNER}, {DEPUTIES}, {MEMBERS}, {MEMBERS-ONLINE}, {MEMBERS-ALL}, {REGION-SIZE}, {POINTS}, {POINTS-FORMAT}, {KILLS}, {DEATHS}, {ASSISTS}, {LOGOUTS}, {KDR}, {ALLIES}, {ALLIES-TAGS}, {ENEMIES}, {ENEMIES-TAGS}, {RANK}, {VALIDITY}, {LIVES}, {LIVES-SYMBOL}, {GUILD-PROTECTION}")
    public List<String> infoList = Arrays.asList(
            "&8-------------------------------",
            "&7Gildia: &c{GUILD} &8[&c{TAG}&8]",
            "&7Zalozyciel: &c{OWNER}",
            "&7Zastepcy: &c{DEPUTIES}",
            "&7Punkty: &c{POINTS} &8[&c{RANK}&8]",
            "&7Ochrona: &c{GUILD-PROTECTION}",
            "&7Zycia: &4{LIVES}",
            "&7Waznosc: &c{VALIDITY}",
            "&7Czlonkowie: &7{MEMBERS}",
            "&7Sojusze: &c{ALLIES}",
            "&7Wojny: &c{ENEMIES}",
            "&8-------------------------------");

    @Comment("<------- Top Messages -------> #")
    @Comment("{GTOP-<pozycja>} - Gildia na podanej pozycji w rankingu")
    public List<String> topList = Arrays.asList(
            "&8----------{ &cTOP 10 &8}----------",
            "&71&8. &c{GTOP-1}",
            "&72&8. &c{GTOP-2}",
            "&73&8. &c{GTOP-3}",
            "&74&8. &c{GTOP-4}",
            "&75&8. &c{GTOP-5}",
            "&76&8. &c{GTOP-6}",
            "&77&8. &c{GTOP-7}",
            "&78&8. &c{GTOP-8}",
            "&79&8. &c{GTOP-9}",
            "&710&8. &c{GTOP-10}");

    @Comment("<------- Ranking Messages -------> #")
    @Comment("{PTOP-<pozycja>} - Gracz na podanej pozycji w rankingu")
    public List<String> rankingList = Arrays.asList(
            "&8----------{ &cTOP 10 Graczy &8}----------",
            "&71&8. &c{PTOP-1}",
            "&72&8. &c{PTOP-2}",
            "&73&8. &c{PTOP-3}",
            "&74&8. &c{PTOP-4}",
            "&75&8. &c{PTOP-5}",
            "&76&8. &c{PTOP-6}",
            "&77&8. &c{PTOP-7}",
            "&78&8. &c{PTOP-8}",
            "&79&8. &c{PTOP-9}",
            "&710&8. &c{PTOP-10}");

    @Comment("<------- General Messages -------> #")
    public String generalHasGuild = "&cMasz juz gildie!";
    public String generalNoNameGiven = "&cPodaj nazwe gildii!";
    public String generalHasNoGuild = "&cNie masz gildii!";
    public String generalIsNotOwner = "&cNie jestes zalozycielem gildii!";
    public String generalNoTagGiven = "&cPodaj tag gildii!";
    public String generalNoNickGiven = "&cPodaj nick gracza!";
    public String generalUserHasGuild = "&cTen gracz ma juz gildie!";
    public String generalNoGuildFound = "&cTaka gildia nie istnieje!";
    public String generalNotPlayedBefore = "&cTen gracz nigdy nie byl na serwerze!";
    public String generalNotOnline = "&cTen gracz nie jest obecnie na serwerze!";

    @Comment("Dostepne zmienne: {TAG}")
    public String generalGuildNotExists = "&7Gildia o tagu &c{TAG} &7nie istnieje!";
    public String generalIsNotMember = "&cTen gracz nie jest czlonkiem twojej gildii!";
    public String generalPlayerHasNoGuild = "&cTen gracz nie ma gildii!";
    public String generalCommandDisabled = "&cTa komenda jest wylaczona!";
    public String generalAllyPvpDisabled = "&cPVP pomiedzy sojuszami jest wylaczone w konfiguracji!";

    @Comment("<------- Escape Messages -------> #")
    public String escapeDisabled = "&cPrzykro mi, ucieczki sa wylaczone!";
    @Comment("Dostepne zmienne: {TIME}")
    public String escapeStartedUser = "&aDobrze, jesli nikt ci nie przeszkodzi - za {TIME} sekund uda ci sie uciec!";
    @Comment("Dostepne zmienne: {TIME}, {X}, {Y}, {Z}, {PLAYER}")
    public String escapeStartedOpponents = "&cGracz {PLAYER} probuje uciec z terenu twojej gildii! ({X}  {Y}  {Z})";
    public String escapeCancelled = "&cUcieczka zostala przerwana!";
    public String escapeInProgress = "&cUcieczka juz trwa!";
    public String escapeSuccessfulUser = "&aUdalo ci sie uciec!";
    @Comment("Dostepne zmienne: {PLAYER}")
    public String escapeSuccessfulOpponents = "&cGraczowi {PLAYER} udalo sie uciec z terenu twojej gildii!";
    public String escapeNoUserGuild = "&cNie masz gildii do ktorej moglbys uciekac!";
    public String escapeNoNeedToRun = "&cNie znajdujesz sie na terenie zadnej gildii, po co uciekac?";
    public String escapeOnYourRegion = "&cZnajdujesz sie na terenie wlasnej gildii, dokad chcesz uciekac?";

    @Comment("<------- Create Guild Messages -------> #")
    @Comment("Dostepne zmienne: {LENGTH}")
    public String createTagLength = "&7Tag nie moze byc dluzszy niz &c{LENGTH} litery&7!";
    @Comment("Dostepne zmienne: {LENGTH}")
    public String createNameLength = "&cNazwa nie moze byc dluzsza niz &c{LENGTH} litery&7!";
    @Comment("Dostepne zmienne: {LENGTH}")
    public String createTagMinLength = "&7Tag nie moze byc krotszy niz &c{LENGTH} litery&7!";
    @Comment("Dostepne zmienne: {LENGTH}")
    public String createNameMinLength = "&cNazwa nie moze byc krotsza niz &c{LENGTH} litery&7!";
    public String createOLTag = "&cTag gildii moze zawierac tylko litery!";
    public String createOLName = "&cNazwa gildii moze zawierac tylko litery!";
    public String createMore = "&cNazwa gildi nie moze zawierac spacji!";
    public String createNameExists = "&cJest juz gildia z taka nazwa!";
    public String createTagExists = "&cJest juz gildia z takim tagiem!";
    public String restrictedGuildName = "&cPodana nazwa gildii jest niedozwolona.";
    public String restrictedGuildTag = "&cPodany tag gildii jest niedozwolony.";
    @Comment("Dostepne zmienne: {DISTANCE}")
    public String createSpawn = "&7Jestes zbyt blisko spawnu! Minimalna odleglosc to &c{DISTANCE}";
    public String createIsNear = "&cW poblizu znajduje sie jakas gildia, poszukaj innego miejsca!";
    @Comment("Dostepne zmienne: {POINTS}, {POINTS-FORMAT}, {REQUIRED}, {REQUIRED-FORMAT}")
    public String createRank = "&cAby zalozyc gildie, wymagane jest przynajmniej &7{REQUIRED} &cpunktow.";
    @Comment("Dostepne zmienne: {ITEM}, {ITEMS}")
    public String createItems = "&cNie masz wszystkich przedmiotow! Obecnie brakuje Ci &7{ITEM} &cz &7{ITEMS}&c. Najedz na przedmiot, aby dowiedziec sie wiecej";
    @Comment("Dostepne zmienne: {EXP}")
    public String createExperience = "&cNie posiadasz wymaganego doswiadczenia do zalozenia gildii: &7{EXP}";
    @Comment("Dostepne zmienne: {MONEY}")
    public String createMoney = "&cNie posiadasz wymaganej ilosci pieniedzy do zalozenia gildii: &7{MONEY}";
    @Comment("Dostepne zmienne: {PLAYER}, {GUILD}, {TAG}")
    public String createGuild = "&7Zalozono gildie o nazwie &a{GUILD} &7i tagu &a{TAG}&7!";
    public String createGuildCouldNotPasteSchematic = "&cWystapil blad podczas tworzenia terenu gildii, zglos sie do administracji.";
    @Comment("Dostepne zmienne: {BORDER-MIN-DISTANCE}")
    public String createNotEnoughDistanceFromBorder = "&cJestes zbyt blisko granicy mapy aby zalozyc gildie! (Minimalna odleglosc: {BORDER-MIN-DISTANCE})";

    @Comment("<------- Delete Guild Messages -------> #")
    public String deleteConfirm = "&7Aby potwierdzic usuniecie gildii, wpisz: &c/potwierdz";
    public String deleteToConfirm = "&cNie masz zadnych dzialan do potwierdzenia!";
    public String deleteSomeoneIsNear = "&cNie mozesz usunac gildii, ktos jest w poblizu!";
    @Comment("Dostepne zmienne: {GUILD}, {TAG}")
    public String deleteSuccessful = "&7Pomyslnie &cusunieto &7gildie!";

    @Comment("<------- Invite Messages -------> #")
    public String invitePlayerExists = "&cNie ma takiego gracza na serwerze!";
    @Comment("Dostepne zmienne: {AMOUNT}")
    public String inviteAmount = "&7Osiagnieto juz &cmaksymalna &7liczbe czlonkow w gildii! (&c{AMOUNT}&7)";
    public String inviteAmountJoin = "&7Ta gildia osiagnela juz &cmaksymalna &7liczbe czlonkow! (&c{AMOUNT}&7)";
    public String inviteAllyAmount = "&7Osiagnieto juz &cmaksymalna &7liczbe sojuszy miedzygildyjnych! (&c{AMOUNT}&7)";
    @Comment("Dostepne zmienne: {AMOUNT}, {GUILD}, {TAG}")
    public String inviteAllyTargetAmount = "&7Gildia {TAG} posiada juz maksymalna liczbe sojuszy! (&c{AMOUNT}&7)";
    public String inviteCancelled = "&cCofnieto zaproszenie!";
    @Comment("Dostepne zmienne: {OWNER}, {GUILD}, {TAG}")
    public String inviteCancelledToInvited = "&7Zaproszenie do gildii &c{GUILD} &7zostalo wycofane!";
    @Comment("Dostepne zmienne: {PLAYER}")
    public String inviteToOwner = "&7Gracz &a{PLAYER} &7zostal zaproszony do gildii!";
    @Comment("Dostepne zmienne: {OWNER}, {GUILD}, {TAG}")
    public String inviteToInvited = "&aOtrzymano zaproszenie do gildii &7{TAG}&a!";

    @Comment("<------- Join Messages -------> #")
    public String joinHasNotInvitation = "&cNie masz zaproszenia do gildii!";
    public String joinHasNotInvitationTo = "&cNie otrzymales zaproszenia do tej gildii!";
    public String joinHasGuild = "&cMasz juz gildie!";
    public String joinTagExists = "&cNie ma gildii o takim tagu!";
    @Comment("Dostepne zmienne: {GUILDS}")
    public List<String> joinInvitationList = Arrays.asList(
            "&7Otrzymano zaproszenia od gildii: &a{GUILDS}",
            "&7Wpisz &a/dolacz [tag] &7aby dolaczyc do wybranej gildii");

    @Comment("Dostepne zmienne: {ITEM}, {ITEMS}")
    public String joinItems = "&cNie masz wszystkich przedmiotow! Obecnie brakuje Ci &7{ITEM} &cz &7{ITEMS}";
    @Comment("Dostepne zmienne: {GUILD}, {TAG}")
    public String joinToMember = "&aDolaczyles do gildii &7{GUILD}";
    @Comment("Dostepne zmienne: {PLAYER}")
    public String joinToOwner = "&a{PLAYER} &7dolaczyl do &aTwojej &7gildii!";

    @Comment("<------- Leave Messages -------> #")
    public String leaveIsOwner = "&cZalozyciel &7nie moze opuscic gildii!";
    @Comment("Dostepne zmienne: {GUILDS}, {TAG}")
    public String leaveToUser = "&7Opusciles gildie &a{GUILD}&7!";

    @Comment("<------- Kick Messages -------> #")
    public String kickOtherGuild = "&cTen gracz nie jest w Twojej gildii!";
    public String kickOwner = "&cNie mozna wyrzucic zalozyciela!";
    @Comment("Dostepne zmienne: {GUILD}, {TAG}, {PLAYER}")
    public String kickToOwner = "&c{PLAYER} &7zostal wyrzucony z gildii!";
    @Comment("Dostepne zmienne: {GUILD}, {TAG}")
    public String kickToPlayer = "&cZostales wyrzucony z gildii!";

    @Comment("<------- Enlarge Messages -------> #")
    public String enlargeMaxSize = "&cOsiagnieto juz maksymalny rozmiar terenu!";
    public String enlargeIsNear = "&cW poblizu znajduje sie jakas gildia, nie mozesz powiekszyc terenu!";
    @Comment("Dostepne zmienne: {ITEM}")
    public String enlargeItem = "&7Nie masz wystarczajacej liczby przedmiotow! Potrzebujesz &c{ITEM}";
    @Comment("Dostepne zmienne: {SIZE}, {LEVEL}")
    public String enlargeDone = "&7Teren &aTwojej &7gildii zostal powiekszony i jego wielkosc wynosi teraz &a{SIZE} &7(poz.&a{LEVEL}&7)";

    @Comment("<------- Base Messages -------> #")
    public String baseTeleportationDisabled = "&cTeleportacja do baz gildyjnych nie jest dostepna";
    public String baseHasNotRegion = "&cTwoja gildia nie posiada terenu!";
    public String baseHasNotCenter = "&cTwoja gildia nie posiada srodka regionu!";
    public String baseIsTeleportation = "&cWlasnie sie teleportujesz!";
    @Comment("Dostepne zmienne: {ITEM}, {ITEMS}")
    public String baseItems = "&cNie masz wszystkich przedmiotow! Obecnie brakuje Ci &7{ITEM} &cz &7{ITEMS}";
    public String baseDontMove = "&7Nie ruszaj sie przez &c{TIME} &7sekund!";
    public String baseMove = "&cRuszyles sie, teleportacja przerwana!";
    public String baseTeleport = "&aTeleportacja&7...";

    @Comment("<------- War Messages -------> #")
    public String enemyCorrectUse = "&7Aby rozpoczac wojne z gildia wpisz &c/wojna [tag]";
    public String enemySame = "&cNie mozesz rozpoczac wojny z wlasna gildia!";
    public String enemyAlly = "&cNie mozesz rozpoczac wojny z ta gildia poniewaz jestescie sojusznikami!";
    public String enemyAlready = "&cProwadzisz juz wojne z ta gildia!";
    @Comment("Dostepne zmienne: {AMOUNT}")
    public String enemyMaxAmount = "&7Osiagnieto juz &cmaksymalna &7liczbe wojen miedzygildyjnych! (&c{AMOUNT}&7)";
    @Comment("Dostepne zmienne: {AMOUNT}, {GUILD}, {TAG}")
    public String enemyMaxTargetAmount = "&7Gildia {TAG} posiada juz maksymalna liczbe wojen! (&c{AMOUNT}&7)";
    @Comment("Dostepne zmienne: {GUILD}, {TAG}")
    public String enemyDone = "&7Wypowiedziano gildii &a{GUILD}&7 wojne!";
    @Comment("Dostepne zmienne: {GUILD}, {TAG}")
    public String enemyIDone = "&7Gildia &a{GUILD} &7wypowiedziala twojej gildii wojne!";
    @Comment("Dostepne zmienne: {GUILD}, {TAG}")
    public String enemyEnd = "&7Zakonczono wojne z gildia &a{GUILD}&7!";
    @Comment("Dostepne zmienne: {GUILD}, {TAG}")
    public String enemyIEnd = "&7Gildia &a{GUILD} &7zakonczyla wojne z twoja gildia!";

    @Comment("<------- Ally Messages -------> #")
    public String allyHasNotInvitation = "&7Aby zaprosic gildie do sojuszy wpisz &c/sojusz [tag]";
    @Comment("Dostepne zmienne: {GUILDS}")
    public List<String> allyInvitationList = Arrays.asList(
            "&7Otrzymano zaproszenia od gildii: &a{GUILDS}",
            "&7Aby zaakceptowac uzyj &a/sojusz [tag]");
    @Comment("Dostepne zmienne: {TAG}")
    public String allyAlly = "&cMasz juz sojusz z ta gildia!";
    public String allyDoesntExist = "&cNie posiadasz sojuszu z ta gildia!";
    public String allySame = "&cNie mozesz nawiazac sojuszu z wlasna gildia!";
    @Comment("Dostepne zmienne: {GUILD}, {TAG}")
    public String allyDone = "&7Nawiazano sojusz z gildia &a{GUILD}&7!";
    @Comment("Dostepne zmienne: {GUILD}, {TAG}")
    public String allyIDone = "&7Gildia &a{GUILD} &7przystapila do sojuszu!";
    @Comment("Dostepne zmienne: {GUILD}, {TAG}")
    public String allyReturn = "&7Wycofano zaproszenie do sojuszu dla gildii &c{GUILD}!";
    @Comment("Dostepne zmienne: {GUILD}, {TAG}")
    public String allyIReturn = "&7Gildia &c{GUILD} &7wycofala zaprszenie do sojuszu!";
    @Comment("Dostepne zmienne: {GUILD}, {TAG}")
    public String allyInviteDone = "&7Zaproszono gildie &a{GUILD} &7do sojuszu!";
    @Comment("Dostepne zmienne: {GUILD}, {TAG}")
    public String allyToInvited = "&7Otrzymano zaproszenie do sojuszu od gildii &a{GUILD}&7!";

    @Comment("<------- Break Messages -------> #")
    public String breakHasNotAllies = "&cTwoja gildia nie posiada sojuszy!";
    @Comment("Dostepne zmienne: {GUILDS}")
    public List<String> breakAlliesList = Arrays.asList(
            "&7Twoja gildia nawiazala sojusz z &a{GUILDS}",
            "&7Aby rozwiazac sojusz wpisz &c/rozwiaz [tag]");
    @Comment("Dostepne zmienne: {GUILD}, {TAG}")
    public String breakAllyExists = "&7Twoja gildia nie posiada sojuszu z gildia (&c{TAG}&7&c{GUILD}&7)!";
    @Comment("Dostepne zmienne: {GUILD}, {TAG}")
    public String breakDone = "&7Rozwiazano sojusz z gildia &c{GUILD}&7!";
    @Comment("Dostepne zmienne: {GUILD}, {TAG}")
    public String breakIDone = "&7Gildia &c{GUILD} &7rozwiazala sojusz z Twoja gildia!";

    @Comment("<------- Validity Messages -------> #")
    @Comment("Dostepne zmienne: {TIME}")
    public String validityWhen = "&7Gildie mozesz przedluzyc dopiero za &c{TIME}&7!";
    @Comment("Dostepne zmienne: {ITEM}")
    public String validityItems = "&7Nie masz wystarczajacej liczby przedmiotow! Potrzebujesz &c{ITEM}";
    @Comment("Dostepne zmienne: {DATE}")
    public String validityDone = "&7Waznosc gildii przedluzona do &a{DATE}&7!";

    @Comment("<------- War Messages -------> #")
    public String warDisabled = "&cPodbijanie gildii jest wyłączone.";
    public String warHasNotGuild = "&cMusisz miec gildie, aby zaatkowac inna!";
    public String warAlly = "&cNie mozesz zaatakowac sojusznika!";
    @Comment("Dostepne zmienne: {TIME}")
    public String warWait = "&7Atak na gildie mozliwy za &4{TIME}";
    @Comment("Dostepne zmienne: {ATTACKED}")
    public String warAttacker = "&7Twoja gildia pozbawila gildie &4{ATTACKED} &7z &41 zycia&7!";
    @Comment("Dostepne zmienne: {ATTACKER}")
    public String warAttacked = "&7Twoja gildia stracila &41 zycie &7przez &4{ATTACKER}&7!";
    @Comment("Dostepne zmienne: {LOSER}")
    public String warWin = "&7Twoja gildia &apodbila &7gildie &a{LOSER}&7! Zyskujecie &c1 zycie&7!";
    @Comment("Dostepne zmienne: {WINNER}")
    public String warLose = "&7Twoja gildia &4przegrala &7wojne z gildia &4{WINNER}&7! &4Gildia zostaje zniszona&7!";

    @Comment("<------- Leader Messages -------> #")
    public String leaderMustBeDifferent = "&cNie mozesz sobie oddac zalozyciela!";
    public String leaderSet = "&7Ustanowiono nowego &alidera &7gildii!";
    public String leaderOwner = "&7Zostales nowym &aliderem &7gildii!";
    @Comment("Dostepne zmienne: {PLAYER}")
    public String leaderMembers = "&7{PLAYER} zostal nowym &aliderem &7gildii!";

    @Comment("<------- TNT Hours Messages -------> #")
    public String tntInfo = "&7TNT na teranach gildii działa od {PROTECTION_END} do {PROTECTION_START}";
    public String tntProtectDisable = "&7TNT wybucha o każdej porze.";
    public String tntNowEnabled = "&aTNT aktualnie jest włączone.";
    public String tntNowDisabled = "&cTNT aktualnie jest wyłączone.";

    @Comment("<------- Deputy Messages -------> #")
    public String deputyMustBeDifferent = "&cNie mozesz mianowac siebie zastepca!";
    public String deputyRemove = "&7Zdegradowno gracza z funkcji &czastepcy&7!";
    public String deputyMember = "&7Zdegradowano Cie z funkcji &czastepcy&7!";
    public String deputySet = "&7Ustanowiono nowego &azastepce &7gildii!";
    public String deputyOwner = "&7Zostales nowym &azastepca &7gildii!";
    @Comment("Dostepne zmienne: {PLAYER}")
    public String deputyMembers = "&7{PLAYER} zostal nowym &azastepca &7gildii!";
    @Comment("Dostepne zmienne: {PLAYER}")
    public String deputyNoLongerMembers = "&7{PLAYER} juz nie jest &azastepca &7gildii!";

    @Comment("<------- Setbase Messages -------> #")
    public String setbaseOutside = "&cNie mozna ustawic domu gildii poza jej terenem!";
    public String setbaseDone = "&7Przeniesiono &adom &7gildii!";

    @Comment("<------- PvP Messages -------> #")
    public String pvpOn = "&cWlaczono pvp w gildii!";
    public String pvpOff = "&aWylaczono pvp w gildii!";
    @Comment("Dostepne zmienne: {TAG}")
    public String pvpAllyOn = "&cWlaczono pvp z sojuszem &7{TAG}!";
    public String pvpAllyOff = "&cWylaczono pvp z sojuszem &7{TAG}!";

    @Comment("<------- Admin Messages -------> #")
    @Comment("Dostepne zmienne: {ADMIN}")
    public String adminGuildBroken = "&cTwoja gildia zostala rozwiazana przez &7{ADMIN}";
    public String adminGuildOwner = "&cTen gracz jest zalozycielem gildii, nie mozna go wyrzucic!";
    public String adminNoRegionFound = "&cGildia nie posiada terenu!";

    public String adminNoPointsGiven = "&cPodaj liczbe punktow!";
    @Comment("Dostepne zmienne: {ERROR}")
    public String adminErrorInNumber = "&cNieznana jest liczba: {ERROR}";
    @Comment("Dostepne zmienne: {PLAYER}, {POINTS}, {POINTS-FORMAT}")
    public String adminPointsChanged = "&aUstawiono &7{POINTS} &apunktow dla gracza &7{PLAYER}";

    public String adminNoKillsGiven = "&cPodaj liczbe zabojstw!";
    @Comment("Dostepne zmienne: {PLAYER}, {KILLS}")
    public String adminKillsChanged = "&aUstawiono &7{KILLS} &azabojstw dla gracza &7{PLAYER}";

    public String adminNoDeathsGiven = "&cPodaj liczbe zgonow!";
    @Comment("Dostepne zmienne: {PLAYER}, {DEATHS}")
    public String adminDeathsChanged = "&aUstawiono &7{DEATHS} &azgonow dla gracza &7{PLAYER}";

    public String adminNoBanTimeGiven = "&cPodaj czas na jaki ma byc zbanowana gildia!";
    public String adminNoReasonGiven = "&cPodaj powod!";
    public String adminGuildBanned = "&cTa gildia jest juz zbanowana!";
    public String adminTimeError = "&cPodano nieprawidlowy czas!";
    @Comment("Dostepne zmienne: {GUILD}, {TIME}")
    public String adminGuildBan = "&aZbanowano gildie &a{GUILD} &7na okres &a{TIME}&7!";

    public String adminGuildNotBanned = "&cTa gildia nie jest zbanowana!";
    @Comment("Dostepne zmienne: {GUILD}")
    public String adminGuildUnban = "&aOdbanowano gildie &7{GUILD}&a!";

    public String adminNoLivesGiven = "&cPodaj liczbe zyc!";
    @Comment("Dostepne zmienne: {GUILD}, {LIVES}")
    public String adminLivesChanged = "&aUstawiono &7{LIVES} &azyc dla gildii &7{GUILD}&a!";

    @Comment("Dostepne zmienne: {GUILD}")
    public String adminGuildRelocated = "&aPrzeniesiono teren gildii &7{GUILD}&a!";

    public String adminNoValidityTimeGiven = "&cPodaj czas o jaki ma byc przedluzona waznosc gildii!";
    @Comment("Dostepne zmienne: {GUILD}, {VALIDITY}")
    public String adminNewValidity = "&aPrzedluzono waznosc gildii &a{GUILD} &7do &a{VALIDITY}&7!";

    public String adminNoNewNameGiven = "&cPodaj nowa nazwe!";
    @Comment("Dostepne zmienne: {GUILD}, {TAG}")
    public String adminNameChanged = "&aZmieniono nazwe gildii na &7{GUILD}&a!";
    public String adminTagChanged = "&aZmieniono tag gildii na &7{TAG}&a!";

    public String adminStopSpy = "&cJuz nie szpiegujesz graczy!";
    public String adminStartSpy = "&aOd teraz szpiegujesz graczy!";

    public String adminGuildsEnabled = "&aZakladanie gildii jest wlaczone!";
    public String adminGuildsDisabled = "&cZakladanie gildii jest wylaczone!";

    public String adminUserNotMemberOf = "&cTen gracz nie jest czlonkiem tej gildii!";
    public String adminAlreadyLeader = "&cTen gracz jest juz liderem gildii!";

    public String adminNoProtectionDateGive = "&cPodaj date ochrony dla gildii! (W formacie: yyyy/mm/dd hh:mm:ss)";
    public String adminInvalidProtectionDate = "&cTo nie jest poprawna data! Poprawny format to: yyyy/mm/dd hh:mm:ss";
    public String adminProtectionSetSuccessfully = "&aPomyslnie ustawiono ochrone dla gildii &7{TAG} &ado &7{DATE}";

    public String adminGuildHasNoHome = "&cGildia gracza nie ma ustawionej bazy!";
    @Comment("Dostepne zmienne: {ADMIN}")
    public String adminTeleportedToBase = "&aAdmin &7{ADMIN} &ateleportowal cie do bazy gildii!";
    @Comment("Dostepne zmienne: {PLAYER}")
    public String adminTargetTeleportedToBase = "&aGracz &7{PLAYER} &azostal teleportowany do bazy gildii!";

    @Comment("<------- SecuritySystem Messages -------> #")
    @Comment("Przedrostek przed wiadomościami systemu bezpieczeństwa")
    public String securitySystemPrefix = "&8[&4Security&8] &7";
    @Comment("Dostepne zmienne: {PLAYER}, {CHEAT}")
    public String securitySystemInfo = "&7Gracz &c{PLAYER}&7 może używać &c{CHEAT}&7 lub innego cheata o podobnym dzialaniu!";
    @Comment("Dostepne zmienne: {NOTE}")
    public String securitySystemNote = "Notatka: &7{NOTE}";
    @Comment("Dostepne zmienne: {DISTANCE}")
    public String securitySystemReach = "&7Zaatakowal krysztal z odleglosci &c{DISTANCE} &7kratek!";
    @Comment("Dostepne zmienne: {BLOCKS}")
    public String securitySystemFreeCam = "Zaatakowal krysztal przez bloki: &c{BLOCKS}";

    @Comment("<------- System Messages -------> #")
    public String reloadWarn = "&cDziałanie pluginu FunnyGuilds po reloadzie moze byc zaburzone, zalecane jest przeprowadzenie restartu serwera!";

    @Override
    public OkaeriConfig load() throws OkaeriException {
        super.load();

        try {
            for (Field field : this.getClass().getDeclaredFields()) {
                if (field.getType().equals(String.class)) {
                    field.set(this, ChatUtils.colored((String) field.get(this)));
                }

                if (field.getType().equals(List.class)) {
                    List<String> list = (List<String>) field.get(this);

                    for (int i = 0; i < list.size(); i++) {
                        list.set(i, ChatUtils.colored(list.get(i)));
                    }
                }
            }
        }
        catch (Exception ex) {
            FunnyGuilds.getPluginLogger().error("Could not load message configuration", ex);
        }

        return this;
    }

}
