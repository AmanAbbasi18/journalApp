package net.engineeringdigest.journalApp.enums;

public enum Sentiment {    //used in journal entry of a user ,it tells the mood of that user throughout the week , don't worry it gets saved in mongoDB as String
    HAPPY,
    SAD,
    ANGRY,
    ANXIOUS;
}
