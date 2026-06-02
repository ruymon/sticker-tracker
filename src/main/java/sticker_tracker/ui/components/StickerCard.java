package sticker_tracker.ui.components;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import sticker_tracker.domain.Sticker;
import sticker_tracker.ui.Theme;

public class StickerCard extends RoundedPanel {

    private static final Map<String, ImageIcon> IMAGE_CACHE = new ConcurrentHashMap<>();
    private static final String QUANTITY_BADGE_PREFIX = "x";
    private static final String TRUNCATED_TEXT_SUFFIX = "…";
    private static final float BORDER_WIDTH = 2f;

    private final Sticker sticker;
    private final boolean collected;
    private final int quantity;

    public StickerCard(Sticker sticker, boolean collected, int quantity) {
        super(Theme.RADIUS_MD);
        this.sticker = sticker;
        this.collected = collected;
        this.quantity = quantity;

        setPreferredSize(new Dimension(Theme.STICKER_CARD_WIDTH, Theme.STICKER_CARD_HEIGHT));
        setBackgroundColor(collected ? Theme.BG_CARD : Theme.BG_SECONDARY);
        buildCard();
    }

    private void buildCard() {
        setLayout(new BorderLayout(Theme.SPACE_NONE, Theme.SPACE_XS));
        setBorder(BorderFactory.createEmptyBorder(
            Theme.SPACE_MD,
            Theme.SPACE_SM,
            Theme.SPACE_SM,
            Theme.SPACE_SM
        ));
        add(buildImageArea(), BorderLayout.CENTER);
        add(buildInfoArea(), BorderLayout.SOUTH);
    }

    private JPanel buildImageArea() {
        final var imageLabel = new JLabel(sticker.getCode(), SwingConstants.CENTER);
        imageLabel.setFont(Theme.FONT_MONO.deriveFont(Theme.SIZE_BASE));
        imageLabel.setForeground(collected ? Theme.TEXT_SECONDARY : Theme.TEXT_MUTED);
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);

        final var imageArea = new JPanel(new GridBagLayout()) {
            @Override
            public void paint(Graphics graphics) {
                if (collected) {
                    super.paint(graphics);
                    return;
                }

                final var graphics2d = (Graphics2D) graphics.create();
                graphics2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Theme.DISABLED_ALPHA));
                super.paint(graphics2d);
                graphics2d.dispose();
            }
        };
        imageArea.setOpaque(false);
        imageArea.setPreferredSize(new Dimension(Theme.STICKER_IMAGE_SIZE, Theme.STICKER_IMAGE_SIZE));
        imageArea.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        imageArea.add(imageLabel);

        if (sticker.getImageUrl() != null && !sticker.getImageUrl().isBlank()) {
            loadImageAsync(sticker.getImageUrl(), imageLabel);
        }

        return imageArea;
    }

    private JPanel buildInfoArea() {
        final var infoArea = new JPanel();
        infoArea.setOpaque(false);
        infoArea.setLayout(new BoxLayout(infoArea, BoxLayout.Y_AXIS));
        infoArea.setAlignmentX(LEFT_ALIGNMENT);

        final var codeRow = new JPanel(new BorderLayout());
        codeRow.setOpaque(false);
        codeRow.setAlignmentX(LEFT_ALIGNMENT);
        codeRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, Theme.SPACE_MD));

        final var codeLabel = new JLabel(sticker.getCode());
        codeLabel.setFont(Theme.FONT_MONO.deriveFont(Theme.SIZE_XS));
        codeLabel.setForeground(Theme.TEXT_MUTED);
        codeRow.add(codeLabel, BorderLayout.WEST);

        if (quantity > 0) {
            final var quantityBadge = new JLabel(QUANTITY_BADGE_PREFIX + quantity);
            quantityBadge.setFont(Theme.FONT_MONO.deriveFont(Theme.SIZE_XS));
            quantityBadge.setForeground(Theme.ACCENT);
            codeRow.add(quantityBadge, BorderLayout.EAST);
        }

        final var nameLabel = new JLabel(truncate(sticker.getName()));
        nameLabel.setAlignmentX(LEFT_ALIGNMENT);
        nameLabel.setHorizontalAlignment(SwingConstants.LEFT);
        nameLabel.setToolTipText(sticker.getName());
        nameLabel.setFont(Theme.FONT_REGULAR.deriveFont(Theme.SIZE_XS));
        nameLabel.setForeground(collected ? Theme.TEXT_PRIMARY : Theme.TEXT_MUTED);

        final var nameRow = new JPanel(new BorderLayout());
        nameRow.setOpaque(false);
        nameRow.setAlignmentX(LEFT_ALIGNMENT);
        nameRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, Theme.SPACE_MD));
        nameRow.add(nameLabel, BorderLayout.WEST);

        infoArea.add(codeRow);
        infoArea.add(nameRow);

        return infoArea;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        final var graphics2d = (Graphics2D) graphics.create();
        graphics2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2d.setStroke(new BasicStroke(BORDER_WIDTH));
        graphics2d.setColor(collected ? Theme.ACCENT : Theme.BORDER);
        graphics2d.drawRoundRect(
            1,
            1,
            getWidth() - 3,
            getHeight() - 3,
            Theme.RADIUS_MD,
            Theme.RADIUS_MD
        );
        graphics2d.dispose();
    }

    private void loadImageAsync(String imageUrl, JLabel imageLabel) {
        if (IMAGE_CACHE.containsKey(imageUrl)) {
            imageLabel.setText("");
            imageLabel.setIcon(IMAGE_CACHE.get(imageUrl));
            return;
        }

        final var worker = new SwingWorker<ImageIcon, Void>() {
            @Override
            protected ImageIcon doInBackground() throws Exception {
                final var loadedImage = ImageIO.read(URI.create(imageUrl).toURL());
                final var scaledImage = loadedImage.getScaledInstance(
                    Theme.STICKER_IMAGE_SIZE,
                    Theme.STICKER_IMAGE_SIZE,
                    Image.SCALE_SMOOTH
                );

                return new ImageIcon(scaledImage);
            }

            @Override
            protected void done() {
                try {
                    final var icon = get();
                    IMAGE_CACHE.put(imageUrl, icon);
                    imageLabel.setText("");
                    imageLabel.setIcon(icon);
                } catch (Exception exception) {
                    imageLabel.setIcon(null);
                }
            }
        };
        worker.execute();
    }

    private String truncate(String text) {
        if (text.length() <= Theme.STICKER_NAME_MAXIMUM_LENGTH) {
            return text;
        }

        return text.substring(0, Theme.STICKER_NAME_MAXIMUM_LENGTH - TRUNCATED_TEXT_SUFFIX.length())
            + TRUNCATED_TEXT_SUFFIX;
    }
}
