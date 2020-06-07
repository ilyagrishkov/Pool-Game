package nl.tudelft.cse.sem.client.screens;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import lombok.Setter;

@Setter
public class ScreenWriter {
    private transient Batch batch;
    private transient BitmapFont titleFont;
    private transient BitmapFont headerFont;
    private transient BitmapFont textFont;

    private transient int drawWidth;
    private transient int drawHeight;
    private transient int drawSpacing;

    public static class Builder {
        transient BitmapFont titleFont;
        transient BitmapFont headerFont;
        transient BitmapFont textFont;
        transient int drawWidth;
        transient int drawHeight;
        transient int drawSpacing;

        /**
         * Title font.
         *
         * @param titleFont - title font
         * @return - Builder
         */
        public Builder withTitleFont(BitmapFont titleFont) {
            this.titleFont = titleFont;

            return this;
        }

        /**
         * Header font.
         *
         * @param headerFont - header font
         * @return - Builder
         */
        public Builder withHeaderFont(BitmapFont headerFont) {
            this.headerFont = headerFont;

            return this;
        }

        /**
         * Text font.
         *
         * @param textFont - text font
         * @return - Builder
         */
        public Builder withTextFont(BitmapFont textFont) {
            this.textFont = textFont;

            return this;
        }

        /**
         * Draw width.
         *
         * @param drawWidth - draw width
         * @return - Builder
         */
        public Builder withDrawWidth(int drawWidth) {
            this.drawWidth = drawWidth;

            return this;
        }

        /**
         * Draw height.
         *
         * @param drawHeight - draw height
         * @return - Builder
         */
        public Builder withDrawHeight(int drawHeight) {
            this.drawHeight = drawHeight;

            return this;
        }

        /**
         * Draw Spacing.
         *
         * @param drawSpacing - draw spacing
         * @return - Builder
         */
        public Builder withDrawSpacing(int drawSpacing) {
            this.drawSpacing = drawSpacing;

            return this;
        }

        /**
         * Build a screen writer.
         *
         * @return - a new ScreenWriter instance
         */
        public ScreenWriter build() {
            ScreenWriter screenWriter = new ScreenWriter();

            screenWriter.batch = new SpriteBatch();
            screenWriter.titleFont = titleFont;
            screenWriter.headerFont = headerFont;
            screenWriter.textFont = textFont;

            screenWriter.drawWidth = drawWidth;
            screenWriter.drawHeight = drawHeight;
            screenWriter.drawSpacing = drawSpacing;

            return screenWriter;
        }
    }

    private ScreenWriter() {
    }


    /**
     * Writes the title.
     * Should be followed by header for correct spacing.
     *
     * @param text The title text.
     */
    public void writeTitle(String text) {
        titleFont.draw(batch, text, drawWidth, drawHeight);
    }

    /**
     * Writes header with additional space on top and bottom.
     *
     * @param text The header text.
     */
    public void writeHeader(String text) {
        headerFont.draw(batch, text, drawWidth, drawHeight -= 3 * drawSpacing);
        drawHeight -= drawSpacing;
    }

    /**
     * Writes normal text line for line.
     *
     * @param text An array of text lines.
     */
    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    public void writeText(String[] text) {
        for (String line : text) {
            textFont.draw(batch, line, drawWidth, drawHeight -= drawSpacing);
        }
    }

    /**
     * Should be called before writing.
     */
    public void begin() {
        batch.begin();
    }

    /**
     * Should be called after writing.
     */
    public void end() {
        batch.end();
    }

    /**
     * Disposes the object.
     */
    public void dispose() {
        batch.dispose();
    }
}
