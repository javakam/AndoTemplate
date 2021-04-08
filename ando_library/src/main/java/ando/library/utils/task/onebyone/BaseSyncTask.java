package ando.library.utils.task.onebyone;

public abstract class BaseSyncTask implements SyncTask {

    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}