package dk.ek.kinoxp.domain;


public final class Theater {
    private final int rows;
    private final int seatsPerRow;
    private final int capacity;

    public static final Theater SAL_1 = new Theater(20, 12);
    public static final Theater SAL_2 = new Theater(22, 16);

    public static Theater of(Integer auditorium) {
        if (auditorium == null) return null;
        return switch (auditorium) {
            case 1 -> SAL_1;
            case 2 -> SAL_2;
            default -> null;
        };
    }

    public Theater(int rows, int seatsPerRow){
        if (rows <= 0 || seatsPerRow <= 0) {
            throw new IllegalArgumentException("rows og seatsPerRow skal være større end 0");
        }
        this.rows = rows;
        this.seatsPerRow = seatsPerRow;
        this.capacity = rows * seatsPerRow;
    }

    public int getRows() {return rows;}
    public int getSeatsPerRow() {return seatsPerRow;}
    public int getCapacity() {return capacity;}
}
