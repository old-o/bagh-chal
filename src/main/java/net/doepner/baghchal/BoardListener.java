package net.doepner.baghchal;

/**
 * Listens to board events
 */
public interface BoardListener {

    void onPredatorTake();

    void onPredatorStep();

    void afterReset();

    BoardListener NONE = new BoardListener() {
        @Override
        public void onPredatorTake() {
            // ignore
        }

        @Override
        public void onPredatorStep() {
            // ignore
        }

        @Override
        public void afterReset() {
            // ignore
        }
    };
}
