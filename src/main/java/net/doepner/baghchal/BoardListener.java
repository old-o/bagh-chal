package net.doepner.baghchal;

/**
 * Listens to board events
 */
public interface BoardListener {

    void onPredatorTake();

    void onPredatorStep();

    BoardListener NONE = new BoardListener() {
        @Override
        public void onPredatorTake() {
            // ignore
        }

        @Override
        public void onPredatorStep() {
            // ignore
        }
    };
}
