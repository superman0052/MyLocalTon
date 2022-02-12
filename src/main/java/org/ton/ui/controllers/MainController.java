package org.ton.ui.controllers;

import com.jfoenix.controls.*;
import javafx.animation.FillTransition;
import javafx.animation.Interpolator;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.PopupWindow;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.ton.actions.MyLocalTon;
import org.ton.db.entities.BlockEntity;
import org.ton.db.entities.TxEntity;
import org.ton.db.entities.WalletEntity;
import org.ton.executors.blockchainexplorer.BlockchainExplorer;
import org.ton.executors.liteclient.LiteClient;
import org.ton.executors.liteclient.LiteClientParser;
import org.ton.executors.liteclient.api.BlockShortSeqno;
import org.ton.executors.liteclient.api.ResultLastBlock;
import org.ton.executors.liteclient.api.ResultListBlockTransactions;
import org.ton.executors.liteclient.api.block.Transaction;
import org.ton.main.App;
import org.ton.parameters.ValidationParam;
import org.ton.settings.MyLocalTonSettings;
import org.ton.utils.Utils;
import org.ton.wallet.WalletVersion;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.LongStream;

import static com.sun.javafx.PlatformUtil.isLinux;
import static com.sun.javafx.PlatformUtil.isWindows;
import static java.util.Objects.requireNonNull;
import static org.ton.actions.MyLocalTon.MAX_ROWS_IN_GUI;
import static org.ton.main.App.fxmlLoader;

@Slf4j
public class MainController implements Initializable {

    public static final String LIGHT_BLUE = "#dbedff";
    public static final String ORANGE = "orange";
    @FXML
    public StackPane superWindow;

    @FXML
    public BorderPane mainWindow;

    @FXML
    public JFXTabPane mainMenuTabs;

    @FXML
    public JFXTabPane settingTabs;

    @FXML
    public Label currentBlockNum;

    @FXML
    public Label liteClientInfo;

    @FXML
    public Label shardsNum;

    @FXML
    public ImageView scrollBtnImageView;

    @FXML
    public HBox topbar;

    @FXML
    public JFXListView<Node> blockslistviewid;

    @FXML
    public JFXListView<Node> transactionsvboxid;

    @FXML
    public JFXListView<Node> accountsvboxid;

    @FXML
    public TextField electedFor;

    @FXML
    public TextField initialBalance;

    @FXML
    public TextField globalId;

    @FXML
    public TextField electionStartBefore;

    @FXML
    public TextField electionEndBefore;

    @FXML
    public TextField stakesFrozenFor;

    @FXML
    public TextField gasPrice;

    @FXML
    public TextField cellPrice;

    @FXML
    public TextField nodeStateTtl;

    @FXML
    public TextField nodeBlockTtl;

    @FXML
    public TextField nodeArchiveTtl;

    @FXML
    public TextField nodeKeyProofTtl;

    @FXML
    public TextField nodeSyncBefore;

    @FXML
    public Tab settingsTab;

    @FXML
    public Tab accountsTab;

    @FXML
    public Tab transactionsTab;

    @FXML
    public JFXButton myLocalTonDbDirBtn;

    @FXML
    public Tab logsTab;

    @FXML
    public Tab validationTab;

    @FXML
    public JFXTabPane validationTabs;

    @FXML
    public JFXTextField nodePublicPort2;

    @FXML
    public JFXTextField nodeConsolePort2;

    @FXML
    public JFXTextField liteServerPort2;

    @FXML
    public Tab fullnode2;

    @FXML
    public Tab fullnode3;

    @FXML
    public JFXTextField nodePublicPort3;

    @FXML
    public JFXTextField nodeConsolePort3;

    @FXML
    public JFXTextField liteServerPort3;

    @FXML
    public Label nodeStatus2;

    @FXML
    public Label nodeStatus3;

    @FXML
    public Tab genesisnode1;

    @FXML
    public Label nodePublicPort1;

    @FXML
    public Label nodeConsolePort1;

    @FXML
    public Label liteServerPort1;

    @FXML
    public Label nodeStatus1;

    @FXML
    public Label totalNodes;

    @FXML
    public Label totalValidators;

    @FXML
    public JFXCheckBox enableBlockchainExplorer;

    @FXML
    public Label enableBlockchainExplorerLabel;

    @FXML
    public Tab explorerTab;

    @FXML
    public WebView webView;

    @FXML
    public Label validator1WalletBalance;

    @FXML
    public Label validator1DbSize;

    @FXML
    public Label validator1WalletAddress;

    @FXML
    public Label validator1AdnlAddress;

    @FXML
    public Label blockchainLaunched;

    @FXML
    public Label startCycle;

    @FXML
    public Label endCycle;

    @FXML
    public Label startElections;

    @FXML
    public Label endElections;

    @FXML
    public Label nextElections;

    @FXML
    public Label minterAddr;

    @FXML
    public Label configAddr;

    @FXML
    public Label electorAddr;

    @FXML
    public Label validationPeriod;

    @FXML
    public Label electionPeriod;

    @FXML
    public Label holdPeriod;

    @FXML
    public Label minimumStake;

    @FXML
    public Label maximumStake;

    @FXML
    public Label validator1PubKey;

    @FXML
    public ProgressBar validationCountDown;

    @FXML
    public Label minterBalance;

    @FXML
    public Label configBalance;

    @FXML
    public Label electorBalance;

    @FXML
    public Label legendHoldStake;

    @FXML
    public Label legendValidation;

    @FXML
    public Label legendElections;

    @FXML
    public Label legendPause;

    @FXML
    public Label stakeHoldRange3;

    @FXML
    public Label validationRange3;

    @FXML
    public Label pauseRange3;

    @FXML
    public Label electionsRange3;

    @FXML
    public Label stakeHoldRange2;

    @FXML
    public Label validationRange2;

    @FXML
    public Label pauseRange2;

    @FXML
    public Label electionsRange2;

    @FXML
    public Label stakeHoldRange1;

    @FXML
    public Label validationRange1;

    @FXML
    public Label pauseRange1;

    @FXML
    public Label electionsRange1;

    @FXML
    public Pane electionsChartPane;

    @FXML
    public Separator timeLine;

    @FXML
    JFXCheckBox shardStateCheckbox;

    @FXML
    JFXCheckBox showMsgBodyCheckBox;

    @FXML
    public Tab searchTab;

    @FXML
    Label searchTabText;

    @FXML
    JFXTextField searchField;

    @FXML
    public Tab foundBlocks;

    @FXML
    public Tab foundAccounts;

    @FXML
    public Tab foundTxs;

    @FXML
    public JFXTabPane foundTabs;

    @FXML
    public JFXListView<Node> foundBlockslistviewid;

    @FXML
    public JFXListView<Node> foundTxsvboxid;

    @FXML
    public JFXListView<Node> foundAccountsvboxid;

    @FXML
    public Tab blocksTab;

    @FXML
    TextField nodePublicPort;

    @FXML
    TextField nodeConsolePort;

    @FXML
    TextField litesServerPort;

    @FXML
    TextField dhtServerPort;

    @FXML
    ImageView aboutLogo;

    @FXML
    JFXTextField gasPriceMc;

    @FXML
    JFXTextField cellPriceMc;

    @FXML
    JFXTextField maxFactor;

    @FXML
    JFXTextField minTotalStake;

    @FXML
    JFXTextField maxStake;

    @FXML
    JFXTextField minStake;

    @FXML
    JFXComboBox<String> walletVersion;

    @FXML
    Label statusBar;

    @FXML
    private JFXButton scrollBtn;

    @FXML
    private JFXSlider walletsNumber;

    @FXML
    private TextField coinsPerWallet;

    @FXML
    private TextField valLogDir;

    @FXML
    private TextField dhtLogDir;

    @FXML
    private TextField minValidators;

    @FXML
    private TextField maxValidators;

    @FXML
    private TextField maxMainValidators;

    @FXML
    private TextField myLocalTonLog;

    @FXML
    private TextField myLocalTonDbLogDir;

    @FXML
    public JFXCheckBox tickTockCheckBox;

    @FXML
    public JFXCheckBox mainConfigTxCheckBox;

    @FXML
    public JFXCheckBox inOutMsgsCheckBox;

    @FXML
    public Label dbSizeId;

    @FXML
    public ComboBox<String> myLogLevel;

    @FXML
    public ComboBox<String> tonLogLevel;

    private MyLocalTonSettings settings;

    JFXDialog sendDialog;
    JFXDialog yesNoDialog;

    public void showSendDialog(String srcAddr) throws IOException {

        Parent parent = new FXMLLoader(App.class.getClassLoader().getResource("org/ton/main/dialogsend.fxml")).load();

        ((Label) parent.lookup("#hiddenWalletAddr")).setText(srcAddr);

        JFXDialogLayout content = new JFXDialogLayout();
        content.setBody(parent);

        sendDialog = new JFXDialog(superWindow, content, JFXDialog.DialogTransition.CENTER);
        sendDialog.setOnKeyPressed(keyEvent -> {
                    if (keyEvent.getCode().equals(KeyCode.ESCAPE)) {
                        sendDialog.close();
                    }
                }
        );
        sendDialog.setOnDialogOpened(jfxDialogEvent -> parent.lookup("#destAddr").requestFocus());
        sendDialog.show();
    }

    public void showInfoMsg(String msg, double durationSeconds) {
        Platform.runLater(() -> {
            statusBar.setStyle("-fx-text-fill: black; -fx-background-color: dbedff");
            Rectangle rect = new Rectangle();
            rect.setFill(Color.valueOf(LIGHT_BLUE));
            statusBar.setBackground(new Background(new BackgroundFill(rect.getFill(), CornerRadii.EMPTY, Insets.EMPTY)));
            statusBar.setText(msg);
            animateBackgroundColor(statusBar, Color.valueOf(LIGHT_BLUE), Color.valueOf(LIGHT_BLUE), (int) (durationSeconds * 1000));
            animateFontColor(statusBar, Color.BLACK, Color.valueOf(LIGHT_BLUE), (int) (durationSeconds * 1000));
        });
    }

    public void showSuccessMsg(String msg, double durationSeconds) {
        Platform.runLater(() -> {
            statusBar.setStyle("-fx-text-fill: white; -fx-background-color: green");
            Rectangle rect = new Rectangle();
            rect.setFill(Color.GREEN);
            statusBar.setBackground(new Background(new BackgroundFill(rect.getFill(), CornerRadii.EMPTY, Insets.EMPTY)));
            statusBar.setText(msg);

            animateBackgroundColor(statusBar, Color.GREEN, Color.valueOf(LIGHT_BLUE), (int) (durationSeconds * 1000));
            animateFontColor(statusBar, Color.WHITE, Color.valueOf(LIGHT_BLUE), (int) (durationSeconds * 1000));
        });
    }

    public void showErrorMsg(String msg, double durationSeconds) {
        Platform.runLater(() -> {
            statusBar.setStyle("-fx-text-fill: black; -fx-background-color: lightcoral");
            Rectangle rect = new Rectangle();
            rect.setFill(Color.valueOf(LIGHT_BLUE));
            statusBar.setBackground(new Background(new BackgroundFill(rect.getFill(), CornerRadii.EMPTY, Insets.EMPTY)));
            statusBar.setText(msg);
            animateBackgroundColor(statusBar, Color.valueOf("lightcoral"), Color.valueOf(LIGHT_BLUE), (int) (durationSeconds * 1000));
            animateFontColor(statusBar, Color.BLACK, Color.valueOf(LIGHT_BLUE), (int) (durationSeconds * 1000));
        });
    }

    public void showWarningMsg(String msg, double durationSeconds) {
        Platform.runLater(() -> {
            statusBar.setStyle("-fx-text-fill: black; -fx-background-color: orange");
            Rectangle rect = new Rectangle();
            rect.setFill(Color.valueOf(ORANGE));
            statusBar.setBackground(new Background(new BackgroundFill(rect.getFill(), CornerRadii.EMPTY, Insets.EMPTY)));
            statusBar.setText(msg);
        });
    }

    public void showShutdownMsg(String msg, double durationSeconds) {
        Platform.runLater(() -> {
            statusBar.setStyle("-fx-text-fill: black; -fx-background-color: orange");
            Rectangle rect = new Rectangle();
            rect.setFill(Color.valueOf(ORANGE));
            statusBar.setBackground(new Background(new BackgroundFill(rect.getFill(), CornerRadii.EMPTY, Insets.EMPTY)));
            statusBar.setText(msg);

            ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
            service.schedule(() -> {
                log.info("final closing");
                saveSettings();
                Platform.exit(); // closes main form

                if (Utils.doShutdown()) {
                    log.info("system exit 0");
                    System.exit(0);
                }
            }, 3, TimeUnit.SECONDS);
        });
    }

    public static void animateBackgroundColor(Control control, Color fromColor, Color toColor, int duration) {

        Rectangle rect = new Rectangle();
        rect.setFill(fromColor);

        Rectangle rectFont = new Rectangle();
        rectFont.setFill(Color.BLACK);

        FillTransition tr = new FillTransition();
        tr.setShape(rect);
        tr.setDuration(Duration.millis(1000));
        tr.setFromValue(fromColor);
        tr.setToValue(toColor);

        tr.setInterpolator(new Interpolator() {
            @Override
            protected double curve(double t) {
                control.setBackground(new Background(new BackgroundFill(rect.getFill(), CornerRadii.EMPTY, Insets.EMPTY)));
                return t;
            }
        });
        tr.setDelay(Duration.millis(duration));
        tr.play();
    }

    public static void animateFontColor(Control control, Color fromColor, Color toColor, int duration) {

        Rectangle rect = new Rectangle();
        rect.setFill(fromColor);

        FillTransition tr = new FillTransition();
        tr.setShape(rect);
        tr.setDuration(Duration.millis(1000));
        tr.setFromValue(fromColor);
        tr.setToValue(toColor);

        tr.setInterpolator(new Interpolator() {
            @Override
            protected double curve(double t) {
                ((Label) control).setTextFill(rect.getFill());
                return t;
            }
        });
        tr.setDelay(Duration.millis(duration));
        tr.play();
    }

    public void shutdown() {
        saveSettings();
    }

    @FXML
    void myLocalTonFileBtnAction() throws IOException {
        log.info("open mylocalton log {}", myLocalTonLog.getText().trim());
        if (isWindows()) {
            Runtime.getRuntime().exec("cmd /c start notepad " + myLocalTonLog.getText());
        } else {
            Runtime.getRuntime().exec("gio open " + myLocalTonLog.getText());
        }
    }

    @FXML
    void myLocalTonDbDirBtnAction() throws IOException {
        log.debug("open mylocalton db dir {}", myLocalTonDbLogDir.getText().trim());
        if (isWindows()) {
            Runtime.getRuntime().exec("cmd /c start " + myLocalTonDbLogDir.getText());
        } else {
            Runtime.getRuntime().exec("gio open " + myLocalTonDbLogDir.getText());
        }
    }

    @FXML
    void dhtLogDirBtnAction() throws IOException {
        log.debug("open dht dir {}", dhtLogDir.getText().trim());
        if (isWindows()) {
            Runtime.getRuntime().exec("cmd /c start " + dhtLogDir.getText());
        } else {
            Runtime.getRuntime().exec("gio open " + dhtLogDir.getText());
        }
    }

    @FXML
    void valLogDirBtnAction() throws IOException {
        log.debug("open validator log dir {}", valLogDir.getText().trim());
        if (isWindows()) {
            Runtime.getRuntime().exec("cmd /c start " + valLogDir.getText());
        } else {
            Runtime.getRuntime().exec("gio open " + valLogDir.getText());
        }
    }

    @FXML
    void blocksOnScroll(ScrollEvent event) {

        Node n1 = blockslistviewid.lookup(".scroll-bar");

        if (n1 instanceof ScrollBar) {
            ScrollBar bar = (ScrollBar) n1;

            if (event.getDeltaY() < 0 && bar.getValue() > 0) { // bottom reached
                Platform.runLater(() -> {
                    BorderPane bp = (BorderPane) blockslistviewid.getItems().get(blockslistviewid.getItems().size() - 1);
                    long lastSeqno = Long.parseLong(((Label) ((Node) bp).lookup("#seqno")).getText());
                    long wc = Long.parseLong(((Label) ((Node) bp).lookup("#wc")).getText());

                    long createdAt = Utils.datetimeToTimestamp(((Label) ((Node) bp).lookup("#createdat")).getText());

                    log.debug("bottom reached, seqno {}, time {}, hwm {} ", lastSeqno, Utils.toUtcNoSpace(createdAt), MyLocalTon.getInstance().getBlocksScrollBarHighWaterMark().get());

                    if (lastSeqno == 1L && wc == -1L) {
                        return;
                    }

                    if (blockslistviewid.getItems().size() > MAX_ROWS_IN_GUI) {
                        showWarningMsg("Maximum amount (" + MyLocalTon.getInstance().getBlocksScrollBarHighWaterMark().get() + ") of visible blocks in GUI reached.", 5);
                        return;
                    }

                    List<BlockEntity> blocks = App.dbPool.loadBlocksBefore(createdAt);
                    MyLocalTon.getInstance().getBlocksScrollBarHighWaterMark().addAndGet(blocks.size());

                    ObservableList<Node> blockRows = FXCollections.observableArrayList();

                    for (BlockEntity block : blocks) {
                        try {
                            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("blockrow.fxml"));
                            javafx.scene.Node blockRow = fxmlLoader.load();

                            ResultLastBlock resultLastBlock = ResultLastBlock.builder()
                                    .createdAt(block.getCreatedAt())
                                    .seqno(block.getSeqno())
                                    .rootHash(block.getRoothash())
                                    .fileHash(block.getFilehash())
                                    .wc(block.getWc())
                                    .shard(block.getShard())
                                    .build();

                            MyLocalTon.getInstance().populateBlockRowWithData(resultLastBlock, blockRow, null);

                            if (resultLastBlock.getWc() == -1L) {
                                blockRow.setStyle("-fx-background-color: e9f4ff;");
                            }
                            log.debug("Adding block {} roothash {}", block.getSeqno(), block.getRoothash());

                            blockRows.add(blockRow);

                        } catch (IOException e) {
                            log.error("Error loading blockrow.fxml file, {}", e.getMessage());
                            return;
                        }
                    }

                    log.debug("blockRows.size  {}", blockRows.size());

                    if ((blockRows.isEmpty()) && (lastSeqno < 10)) {
                        log.debug("On start some blocks were skipped, load them now from 1 to {}", lastSeqno - 1);

                        LongStream.range(1, lastSeqno).forEach(i -> { // TODO for loop big integer
                            try {
                                ResultLastBlock block = LiteClientParser.parseBySeqno(new LiteClient().executeBySeqno(MyLocalTon.getInstance().getSettings().getGenesisNode(), -1L, "8000000000000000", new BigInteger(String.valueOf(i))));
                                log.debug("Load missing block {}: {}", i, block.getFullBlockSeqno());
                                MyLocalTon.getInstance().insertBlocksAndTransactions(MyLocalTon.getInstance().getSettings().getGenesisNode(), block, false);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    }
                    blockslistviewid.getItems().addAll(blockRows);
                });
            }
            if (event.getDeltaY() > 0) { // top reached
                log.debug("top reached");
            }
        }
    }

    @FXML
    void txsOnScroll(ScrollEvent event) {

        log.debug("txsOnScroll: {}", event);

        Node n1 = transactionsvboxid.lookup(".scroll-bar");

        if (n1 instanceof ScrollBar) {
            ScrollBar bar = (ScrollBar) n1;

            if (event.getDeltaY() < 0 && bar.getValue() > 0) { // bottom reached

                Platform.runLater(() -> {

                    BorderPane bp = (BorderPane) transactionsvboxid.getItems().get(transactionsvboxid.getItems().size() - 1);
                    String shortseqno = ((Label) ((Node) bp).lookup("#block")).getText();

                    long createdAt = Utils.datetimeToTimestamp(((Label) ((Node) bp).lookup("#time")).getText());

                    BlockShortSeqno blockShortSeqno = BlockShortSeqno.builder()
                            .wc(Long.valueOf(StringUtils.substringBetween(shortseqno, "(", ",")))
                            .shard(StringUtils.substringBetween(shortseqno, ",", ","))
                            .seqno(new BigInteger(StringUtils.substring(StringUtils.substringAfterLast(shortseqno, ","), 0, -1)))
                            .build();

                    log.debug("bottom reached, seqno {}, hwm {}, createdAt {}, utc {}", blockShortSeqno.getSeqno(), MyLocalTon.getInstance().getTxsScrollBarHighWaterMark().get(), createdAt, Utils.toUtcNoSpace(createdAt));

                    if (blockShortSeqno.getSeqno().compareTo(BigInteger.ONE) == 0) {
                        return;
                    }

                    if (transactionsvboxid.getItems().size() > MAX_ROWS_IN_GUI) {
                        showWarningMsg("Maximum amount (" + MyLocalTon.getInstance().getTxsScrollBarHighWaterMark().get() + ") of visible TXs in GUI reached.", 5);
                        return;
                    }

                    List<TxEntity> txs = App.dbPool.loadTxsBefore(createdAt);

                    MyLocalTon.getInstance().applyTxGuiFilters(txs);

                    MyLocalTon.getInstance().getTxsScrollBarHighWaterMark().addAndGet(txs.size());

                    ObservableList<Node> txRows = FXCollections.observableArrayList();

                    for (TxEntity txEntity : txs) {
                        try {
                            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("txrow.fxml"));
                            javafx.scene.Node txRow = fxmlLoader.load();

                            String shortBlock = String.format("(%d,%s,%d)", txEntity.getWc(), txEntity.getShard(), txEntity.getSeqno());

                            ResultListBlockTransactions resultListBlockTransactions = ResultListBlockTransactions.builder()
                                    .txSeqno(new BigInteger(txEntity.getSeqno().toString()))
                                    .hash(txEntity.getTxHash())
                                    .accountAddress(txEntity.getTx().getAccountAddr())
                                    .lt(txEntity.getTx().getLt())
                                    .build();

                            Transaction txDetails = Transaction.builder()
                                    .accountAddr(txEntity.getTx().getAccountAddr())
                                    .description(txEntity.getTx().getDescription())
                                    .inMsg(txEntity.getTx().getInMsg())
                                    .endStatus(txEntity.getTx().getEndStatus())
                                    .now(txEntity.getTx().getNow())
                                    .totalFees(txEntity.getTx().getTotalFees())
                                    .lt(new BigInteger(txEntity.getTxLt().toString()))
                                    .build();

                            MyLocalTon.getInstance().populateTxRowWithData(shortBlock, resultListBlockTransactions, txDetails, txRow, txEntity);

                            if (txEntity.getTypeTx().equals("Message")) {
                                txRow.setStyle("-fx-background-color: e9f4ff;");
                            }

                            log.debug("adding tx hash {}, addr {}", txEntity.getTxHash(), txEntity.getTx().getAccountAddr());

                            txRows.add(txRow);

                        } catch (IOException e) {
                            log.error("error loading txrow.fxml file, {}", e.getMessage());
                            return;
                        }
                    }
                    log.debug("txRows.size  {}", txRows.size());

                    if ((txRows.isEmpty()) && (blockShortSeqno.getSeqno().compareTo(BigInteger.TEN) < 0)) {
                        log.debug("on start some blocks were skipped and thus some transactions get lost, load them from blocks 1");

                        LongStream.range(1, blockShortSeqno.getSeqno().longValue()).forEach(i -> {
                            try {
                                ResultLastBlock block = LiteClientParser.parseBySeqno(new LiteClient().executeBySeqno(MyLocalTon.getInstance().getSettings().getGenesisNode(), -1L, "8000000000000000", new BigInteger(String.valueOf(i))));
                                log.debug("load missing block {}: {}", i, block.getFullBlockSeqno());
                                MyLocalTon.getInstance().insertBlocksAndTransactions(MyLocalTon.getInstance().getSettings().getGenesisNode(), block, false);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    }
                    transactionsvboxid.getItems().addAll(txRows);
                });
            }
            if (event.getDeltaY() > 0) { // top reached
                log.debug("top reached");
            }
        }
    }

    @FXML
    void scrollBtnAction() {
        MyLocalTon.getInstance().setAutoScroll(!MyLocalTon.getInstance().getAutoScroll());

        if (Boolean.TRUE.equals(MyLocalTon.getInstance().getAutoScroll())) {
            scrollBtnImageView.setImage(new Image(requireNonNull(getClass().getResourceAsStream("/org/ton/images/scroll.png"))));
        } else {
            scrollBtnImageView.setImage(new Image(requireNonNull(getClass().getResourceAsStream("/org/ton/images/scrolloff.png"))));
        }
        log.debug("auto scroll {}", MyLocalTon.getInstance().getAutoScroll());
    }

    private void showLoading(ActionEvent event) throws IOException {
        Stage stage = new Stage();
        stage.initStyle(StageStyle.UNDECORATED);
        //stage.initStyle(StageStyle.TRANSPARENT);
        //stage.setFill(Color.TRANSPARENT);
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("modal_progress" + ".fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);

        stage.setTitle("My modal window");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(((Node) event.getSource()).getScene().getWindow());
        stage.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        settings = MyLocalTon.getInstance().getSettings();

        WebEngine browser = webView.getEngine();

        walletsNumber.setOnMouseReleased(event -> {
            log.debug("walletsNumber released, {}", walletsNumber.getValue());
        });

        settingTabs.getSelectionModel().selectedItemProperty().addListener(e -> {
            log.debug("settings tab changed, save settings");
            saveSettings();
        });

        mainMenuTabs.getSelectionModel().selectedItemProperty().addListener(e -> {
            log.debug("main menu changed, save settings");
            saveSettings();
        });

        EventHandler<KeyEvent> onlyDigits = keyEvent -> {
            if (!((TextField) keyEvent.getSource()).getText().matches("[\\d\\.\\-]+")) {
                ((TextField) keyEvent.getSource()).setText(((TextField) keyEvent.getSource()).getText().replaceAll("[^\\d\\.\\-]", ""));
            }
        };

        coinsPerWallet.setOnKeyTyped(onlyDigits);

        nodePublicPort.setOnKeyTyped(onlyDigits);
        nodeConsolePort.setOnKeyTyped(onlyDigits);
        litesServerPort.setOnKeyTyped(onlyDigits);
        dhtServerPort.setOnKeyTyped(onlyDigits);

        globalId.setOnKeyTyped(onlyDigits);
        initialBalance.setOnKeyTyped(onlyDigits);
        maxMainValidators.setOnKeyTyped(onlyDigits);
        minValidators.setOnKeyTyped(onlyDigits);
        maxValidators.setOnKeyTyped(onlyDigits);
        electedFor.setOnKeyTyped(onlyDigits);
        electionStartBefore.setOnKeyTyped(onlyDigits);
        electionEndBefore.setOnKeyTyped(onlyDigits);
        stakesFrozenFor.setOnKeyTyped(onlyDigits);
        gasPrice.setOnKeyTyped(onlyDigits);
        gasPriceMc.setOnKeyTyped(onlyDigits);
        cellPrice.setOnKeyTyped(onlyDigits);
        cellPriceMc.setOnKeyTyped(onlyDigits);
        minStake.setOnKeyTyped(onlyDigits);
        maxStake.setOnKeyTyped(onlyDigits);
        minTotalStake.setOnKeyTyped(onlyDigits);
        maxFactor.setOnKeyTyped(onlyDigits);
        electionEndBefore.setOnKeyTyped(onlyDigits);
        nodeStateTtl.setOnKeyTyped(onlyDigits);
        nodeBlockTtl.setOnKeyTyped(onlyDigits);
        nodeArchiveTtl.setOnKeyTyped(onlyDigits);
        nodeKeyProofTtl.setOnKeyTyped(onlyDigits);
        nodeSyncBefore.setOnKeyTyped(onlyDigits);

        searchField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                log.debug("search for {}", searchField.getText());

                foundBlockslistviewid.getItems().clear();
                foundTxsvboxid.getItems().clear();
                foundAccountsvboxid.getItems().clear();

                //clear previous results
                mainMenuTabs.getTabs().add(searchTab);
                mainMenuTabs.getSelectionModel().selectLast();
                foundTabs.getTabs().add(foundBlocks);
                foundTabs.getTabs().add(foundAccounts);
                foundTabs.getTabs().add(foundTxs);

                String searchFor = searchField.getText();

                List<BlockEntity> foundBlocksEntities = App.dbPool.searchBlocks(searchFor);
                MyLocalTon.getInstance().showFoundBlocksInGui(foundBlocksEntities, searchFor);

                List<TxEntity> foundTxsEntities = App.dbPool.searchTxs(searchFor);
                MyLocalTon.getInstance().showFoundTxsInGui(((MainController) fxmlLoader.getController()).foundTxs, foundTxsEntities, searchFor, "");

                List<WalletEntity> foundAccountsEntities = App.dbPool.searchAccounts(searchFor);
                MyLocalTon.getInstance().showFoundAccountsInGui(foundAccountsEntities, searchFor);
            }
        });

        mainMenuTabs.getTabs().remove(searchTab);

        foundTabs.getTabs().remove(foundBlocks);
        foundTabs.getTabs().remove(foundAccounts);
        foundTabs.getTabs().remove(foundTxs);

        scrollBtn.setTooltip(new Tooltip("Autoscroll on/off"));

        tickTockCheckBox.setSelected(settings.getUiSettings().isShowTickTockTransactions());
        mainConfigTxCheckBox.setSelected(settings.getUiSettings().isShowMainConfigTransactions());
        inOutMsgsCheckBox.setSelected(settings.getUiSettings().isShowInOutMessages());
        enableBlockchainExplorer.setSelected(settings.getUiSettings().isEnableBlockchainExplorer());
        showMsgBodyCheckBox.setSelected(settings.getUiSettings().isShowBodyInMessage());
        shardStateCheckbox.setSelected(settings.getUiSettings().isShowShardStateInBlockDump());

        walletsNumber.setValue(settings.getWalletSettings().getNumberOfPreinstalledWallets());
        coinsPerWallet.setText(settings.getWalletSettings().getInitialAmount().toString());
        walletVersion.getItems().add(WalletVersion.V1.getValue());
        walletVersion.getItems().add(WalletVersion.V2.getValue());
        walletVersion.getItems().add(WalletVersion.V3.getValue());
        walletVersion.getSelectionModel().select(settings.getWalletSettings().getWalletVersion().getValue());

        valLogDir.setText(settings.getGenesisNode().getTonDbDir());
        myLocalTonLog.setText(settings.LOG_FILE);
        myLocalTonDbLogDir.setText(settings.DB_DIR);
        dhtLogDir.setText(settings.getGenesisNode().getDhtServerDir());

        minValidators.setText(settings.getBlockchainSettings().getMinValidators().toString());
        maxValidators.setText(settings.getBlockchainSettings().getMaxValidators().toString());
        maxMainValidators.setText(settings.getBlockchainSettings().getMaxMainValidators().toString());

        electedFor.setText(settings.getBlockchainSettings().getElectedFor().toString());
        electionStartBefore.setText(settings.getBlockchainSettings().getElectionStartBefore().toString());
        electionEndBefore.setText(settings.getBlockchainSettings().getElectionEndBefore().toString());
        stakesFrozenFor.setText(settings.getBlockchainSettings().getElectionStakesFrozenFor().toString());

        globalId.setText(settings.getBlockchainSettings().getGlobalId().toString());
        initialBalance.setText(settings.getBlockchainSettings().getInitialBalance().toString());
        gasPrice.setText(settings.getBlockchainSettings().getGasPrice().toString());
        gasPriceMc.setText(settings.getBlockchainSettings().getGasPriceMc().toString());
        cellPrice.setText(settings.getBlockchainSettings().getCellPrice().toString());
        cellPriceMc.setText(settings.getBlockchainSettings().getCellPriceMc().toString());

        minStake.setText(settings.getBlockchainSettings().getMinValidatorStake().toString());
        maxStake.setText(settings.getBlockchainSettings().getMaxValidatorStake().toString());
        minTotalStake.setText(settings.getBlockchainSettings().getMinTotalValidatorStake().toString());
        maxFactor.setText(settings.getBlockchainSettings().getMaxFactor().toString());

        nodeBlockTtl.setText(settings.getBlockchainSettings().getValidatorBlockTtl().toString());
        nodeArchiveTtl.setText(settings.getBlockchainSettings().getValidatorArchiveTtl().toString());
        nodeKeyProofTtl.setText(settings.getBlockchainSettings().getValidatorKeyProofTtl().toString());
        nodeStateTtl.setText(settings.getBlockchainSettings().getValidatorStateTtl().toString());
        nodeSyncBefore.setText(settings.getBlockchainSettings().getValidatorSyncBefore().toString());

        nodePublicPort.setText(settings.getGenesisNode().getPublicPort().toString());
        nodeConsolePort.setText(settings.getGenesisNode().getConsolePort().toString());
        litesServerPort.setText(settings.getGenesisNode().getLiteServerPort().toString());
        dhtServerPort.setText(settings.getGenesisNode().getDhtPort().toString());

        tonLogLevel.getItems().add("DEBUG");
        tonLogLevel.getItems().add("WARNING");
        tonLogLevel.getItems().add("INFO");
        tonLogLevel.getItems().add("ERROR");
        tonLogLevel.getItems().add("FATAL");
        tonLogLevel.getSelectionModel().select(settings.getLogSettings().getTonLogLevel());

        myLogLevel.getItems().add("INFO");
        myLogLevel.getItems().add("DEBUG");
        myLogLevel.getItems().add("ERROR");
        myLogLevel.getSelectionModel().select(settings.getLogSettings().getMyLocalTonLogLevel());

        //if (isWindows()) {
        //mainMenuTabs.getTabs().remove(validationTab); // TODO
        //}

        enableBlockchainExplorer.setVisible(false);
        enableBlockchainExplorerLabel.setVisible(false);
        mainMenuTabs.getTabs().remove(explorerTab);

        //if (isLinux() || isMac()) {
        if (isLinux()) {

            enableBlockchainExplorer.setVisible(true);
            enableBlockchainExplorerLabel.setVisible(true);

            if (enableBlockchainExplorer.isSelected()) {
                mainMenuTabs.getTabs().remove(searchTab);
                mainMenuTabs.getTabs().remove(explorerTab);
                mainMenuTabs.getTabs().add(explorerTab);
            } else {
                mainMenuTabs.getTabs().remove(explorerTab);
            }
        }
    }

    public void startWeb() {

        //if (isLinux() || isMac()) {
        if (isLinux()) {
            if (enableBlockchainExplorer.isSelected()) {
                BlockchainExplorer blockchainExplorer = new BlockchainExplorer();
                blockchainExplorer.startBlockchainExplorer(settings.getGenesisNode(), settings.getGenesisNode().getNodeGlobalConfigLocation(), 8000);
                WebEngine webEngine = webView.getEngine();
                webEngine.load("http://127.0.0.1:8000/last");
            }
        }
    }

    public void showAccTxs(String hexAddr) throws IOException {

        mainMenuTabs.getTabs().remove(searchTab);
        mainMenuTabs.getTabs().add(searchTab);
        mainMenuTabs.getSelectionModel().selectLast();

        if (!foundTabs.getTabs().filtered(t -> t.getText().contains(Utils.getLightAddress(hexAddr))).isEmpty()) {
            return;
        }

        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("foundtxstab.fxml"));
        Tab newTab = fxmlLoader.load();

        newTab.setOnClosed(event -> {
            if (foundTabs.getTabs().isEmpty()) {
                mainMenuTabs.getTabs().remove(searchTab);
                mainMenuTabs.getSelectionModel().selectFirst();
            }
        });

        foundTabs.getTabs().add(newTab);

        List<TxEntity> foundTxsEntities = App.dbPool.searchTxs(hexAddr);
        MyLocalTon.getInstance().showFoundTxsInGui(newTab, foundTxsEntities, hexAddr, hexAddr);
        foundTabs.getSelectionModel().selectLast();
    }

    void saveSettings() {
        log.debug("saving all settings");
        settings.getUiSettings().setShowTickTockTransactions(tickTockCheckBox.isSelected());
        settings.getUiSettings().setShowMainConfigTransactions(mainConfigTxCheckBox.isSelected());
        settings.getUiSettings().setShowInOutMessages(inOutMsgsCheckBox.isSelected());
        settings.getUiSettings().setShowBodyInMessage(showMsgBodyCheckBox.isSelected());
        settings.getUiSettings().setEnableBlockchainExplorer(enableBlockchainExplorer.isSelected());
        settings.getUiSettings().setShowShardStateInBlockDump(shardStateCheckbox.isSelected());

        settings.getWalletSettings().setNumberOfPreinstalledWallets((long) walletsNumber.getValue());
        settings.getWalletSettings().setInitialAmount(Long.valueOf(coinsPerWallet.getText()));
        settings.getWalletSettings().setWalletVersion(WalletVersion.getKeyByValue(walletVersion.getValue()));

        settings.getBlockchainSettings().setMinValidators(Long.valueOf(minValidators.getText()));
        settings.getBlockchainSettings().setMaxValidators(Long.valueOf(maxValidators.getText()));
        settings.getBlockchainSettings().setMaxMainValidators(Long.valueOf(maxMainValidators.getText()));

        settings.getBlockchainSettings().setGlobalId(Long.valueOf(globalId.getText()));
        settings.getBlockchainSettings().setInitialBalance(Long.valueOf(initialBalance.getText()));

        settings.getBlockchainSettings().setElectedFor(Long.valueOf(electedFor.getText()));
        settings.getBlockchainSettings().setElectionStartBefore(Long.valueOf(electionStartBefore.getText()));
        settings.getBlockchainSettings().setElectionEndBefore(Long.valueOf(electionEndBefore.getText()));
        settings.getBlockchainSettings().setElectionStakesFrozenFor(Long.valueOf(stakesFrozenFor.getText()));
        settings.getBlockchainSettings().setGasPrice(Long.valueOf(gasPrice.getText()));
        settings.getBlockchainSettings().setGasPriceMc(Long.valueOf(gasPriceMc.getText()));
        settings.getBlockchainSettings().setCellPrice(Long.valueOf(cellPrice.getText()));
        settings.getBlockchainSettings().setCellPriceMc(Long.valueOf(cellPriceMc.getText()));

        settings.getBlockchainSettings().setMinValidatorStake(Long.valueOf(minStake.getText()));
        settings.getBlockchainSettings().setMaxValidatorStake(Long.valueOf(maxStake.getText()));
        settings.getBlockchainSettings().setMinTotalValidatorStake(Long.valueOf(minTotalStake.getText()));
        settings.getBlockchainSettings().setMaxFactor(Long.valueOf(maxFactor.getText()));

        settings.getBlockchainSettings().setValidatorBlockTtl(Long.valueOf(nodeBlockTtl.getText()));
        settings.getBlockchainSettings().setValidatorArchiveTtl(Long.valueOf(nodeArchiveTtl.getText()));
        settings.getBlockchainSettings().setValidatorKeyProofTtl(Long.valueOf(nodeKeyProofTtl.getText()));
        settings.getBlockchainSettings().setValidatorStateTtl(Long.valueOf(nodeStateTtl.getText()));
        settings.getBlockchainSettings().setValidatorSyncBefore(Long.valueOf(nodeSyncBefore.getText()));

        settings.getLogSettings().setTonLogLevel(tonLogLevel.getValue());
        settings.getLogSettings().setMyLocalTonLogLevel(myLogLevel.getValue());

        settings.getGenesisNode().setPublicPort(Integer.valueOf(nodePublicPort.getText()));
        settings.getGenesisNode().setConsolePort(Integer.valueOf(nodeConsolePort.getText()));
        settings.getGenesisNode().setLiteServerPort(Integer.valueOf(litesServerPort.getText()));
        settings.getGenesisNode().setDhtPort(Integer.valueOf(dhtServerPort.getText()));

        settings.saveSettingsToGson(settings);
    }

    public void accountsOnScroll(ScrollEvent scrollEvent) {
        log.debug("accountsOnScroll");
    }

    public void foundBlocksOnScroll(ScrollEvent scrollEvent) {
        log.debug("foundBlocksOnScroll");
    }

    public void foundTxsOnScroll(ScrollEvent scrollEvent) {
        log.debug("foundTxsOnScroll");
    }

    public void liteServerClicked() throws IOException {
        String lastCommand = new LiteClient().getLastCommand(MyLocalTon.getInstance().getSettings().getGenesisNode());
        log.info("show console with last command, {}", lastCommand);

        if (isWindows()) {
            log.info("cmd /c start cmd.exe /k \"echo " + lastCommand + " && " + lastCommand + "\"");
            Runtime.getRuntime().exec("cmd /c start cmd.exe /k \"echo " + lastCommand + " && " + lastCommand + "\"");
        } else if (isLinux()) {
            if (Files.exists(Paths.get("/usr/bin/xterm"))) {
                log.info("/usr/bin/xterm -hold -geometry 200 -e " + lastCommand);
                Runtime.getRuntime().exec("/usr/bin/xterm -hold -geometry 200 -e " + lastCommand);
            } else {
                log.info("xterm is not installed");
            }
        } else {
            //log.info("zsh -c \"" + lastCommand + "\"");
            //Runtime.getRuntime().exec("zsh -c \"" + lastCommand + "\"");
            log.debug("terminal call not implemented");
        }

        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString(lastCommand);
        clipboard.setContent(content);
        log.debug(lastCommand + " copied");
        App.mainController.showInfoMsg("lite-client last command copied to clipboard", 0.5);
    }

    public void resetAction() throws IOException {

        Parent parent = new FXMLLoader(App.class.getClassLoader().getResource("org/ton/main/yesnodialog.fxml")).load();
        parent.lookup("#inputFields").setVisible(false);
        parent.lookup("#body").setVisible(true);
        parent.lookup("#header").setVisible(true);
        ((Label) parent.lookup("#action")).setText("reset");
        ((Label) parent.lookup("#header")).setText("Reset TON blockchain");
        ((Label) parent.lookup("#body")).setText("You can reset current single-node TON blockchain to the new settings. All data will be lost and zero state will be created from scratch. Do you want to proceed?");
        parent.lookup("#okBtn").setDisable(false);

        JFXDialogLayout content = new JFXDialogLayout();
        content.setBody(parent);

        yesNoDialog = new JFXDialog(superWindow, content, JFXDialog.DialogTransition.CENTER);
        yesNoDialog.setOnKeyPressed(keyEvent -> {
                    if (keyEvent.getCode().equals(KeyCode.ESCAPE)) {
                        yesNoDialog.close();
                    }
                }
        );

        yesNoDialog.show();
    }

    public void transformAction() throws IOException {

        Parent parent = new FXMLLoader(App.class.getClassLoader().getResource("org/ton/main/yesnodialog.fxml")).load();
        parent.lookup("#inputFields").setVisible(false);
        parent.lookup("#body").setVisible(true);
        parent.lookup("#header").setVisible(true);
        ((Label) parent.lookup("#action")).setText("transform");
        ((Label) parent.lookup("#header")).setText("Transform");
        ((Label) parent.lookup("#body")).setText("You can transform this single-node TON blockchain into three-nodes TON blockchain, where all three nodes will act as validators and participate in elections. " +
                "Later you will be able to add more full nodes if you wish. Do you want to proceed?");
        parent.lookup("#okBtn").setDisable(true);

        JFXDialogLayout content = new JFXDialogLayout();
        content.setBody(parent);

        yesNoDialog = new JFXDialog(superWindow, content, JFXDialog.DialogTransition.CENTER);
        yesNoDialog.setOnKeyPressed(keyEvent -> {
                    if (keyEvent.getCode().equals(KeyCode.ESCAPE)) {
                        yesNoDialog.close();
                    }
                }
        );

        yesNoDialog.show();
    }

    public void showMessage(String msg) {

        try {

            Parent parent = new FXMLLoader(App.class.getClassLoader().getResource("org/ton/main/yesnodialog.fxml")).load();
            parent.lookup("#inputFields").setVisible(false);
            parent.lookup("#body").setVisible(true);
            parent.lookup("#header").setVisible(true);
            ((Label) parent.lookup("#action")).setText("showmsg");
            ((Label) parent.lookup("#header")).setText("Message");
            ((Label) parent.lookup("#body")).setText(msg);
            parent.lookup("#okBtn").setDisable(false);
            ((JFXButton) parent.lookup("#okBtn")).setText("Close");

            JFXDialogLayout content = new JFXDialogLayout();
            content.setBody(parent);

            yesNoDialog = new JFXDialog(superWindow, content, JFXDialog.DialogTransition.CENTER);
            yesNoDialog.setOnKeyPressed(keyEvent -> {
                        if (keyEvent.getCode().equals(KeyCode.ESCAPE)) {
                            yesNoDialog.close();
                        }
                    }
            );

            yesNoDialog.show();
        } catch (Exception e) {
            log.error("Cannot show message, error {}", e.getMessage());
        }
    }

    public void createNewAccountBtn() throws IOException {
        log.info("create account btn");

        Parent parent = new FXMLLoader(App.class.getClassLoader().getResource("org/ton/main/yesnodialog.fxml")).load();
        ((Label) parent.lookup("#action")).setText("create");
        ((Label) parent.lookup("#header")).setText("Create " + settings.getWalletSettings().getWalletVersion());
        parent.lookup("#body").setVisible(false);
        parent.lookup("#inputFields").setVisible(true);
        if (settings.getWalletSettings().getWalletVersion().equals(WalletVersion.V3)) {
            parent.lookup("#workchain").setVisible(true);
            parent.lookup("#subWalletId").setVisible(true);
        } else {
            parent.lookup("#workchain").setVisible(true);
            parent.lookup("#subWalletId").setVisible(false);
        }
        parent.lookup("#okBtn").setDisable(false);

        JFXDialogLayout content = new JFXDialogLayout();
        content.setBody(parent);

        yesNoDialog = new JFXDialog(superWindow, content, JFXDialog.DialogTransition.CENTER);
        yesNoDialog.setOnKeyPressed(keyEvent -> {
                    if (keyEvent.getCode().equals(KeyCode.ESCAPE)) {
                        yesNoDialog.close();
                    }
                }
        );

        yesNoDialog.show();
    }

    public void showConfiguration() throws Exception {
        ValidationParam v = Utils.getConfig(settings.getGenesisNode());
        log.info("validation params {}", v);

        Utils.updateValidationTabGUI(v);
    }

    public void testValidator() throws Exception {

        Executors.newSingleThreadExecutor().submit(() -> {
            Thread.currentThread().setName("MyLocalTon - Accounts Monitor");
            try {

                org.ton.settings.Node node2 = settings.getNode2();
//                org.ton.settings.Node node3 = settings.getNode3();
//                org.ton.settings.Node node4 = settings.getNode4();
//                org.ton.settings.Node node5 = settings.getNode5();

                ValidationParam v = Utils.getConfig(settings.getGenesisNode());
                log.info("validation params {}", v);

                Utils.updateValidationTabGUI(v);

//                Executors.newSingleThreadExecutor().submit(() -> {
//                    Thread.currentThread().setName("MyLocalTon - Validator " + node2.getNodeName());
//                    try {
//                        MyLocalTon.getInstance().createFullnode(node2, true, true);
//                        Utils.waitForBlockchainReady(node2);
//                        Utils.waitForNodeSynchronized(node2);
//                        saveSettings();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                });

//                Executors.newSingleThreadExecutor().submit(() -> {
//                    Thread.currentThread().setName("MyLocalTon - Validator " + node3.getNodeName());
//                    try {
//                        MyLocalTon.getInstance().createFullnode(node3, true, true);
//                        Utils.waitForBlockchainReady(node3);
//                        Utils.waitForNodeSynchronized(node3);
//                        saveSettings();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                });
//
//                Executors.newSingleThreadExecutor().submit(() -> {
//                    Thread.currentThread().setName("MyLocalTon - Validator " + node4.getNodeName());
//                    try {
//                        MyLocalTon.getInstance().createFullnode(node4, true, true);
//                        Utils.waitForBlockchainReady(node4);
//                        Utils.waitForNodeSynchronized(node4);
//                        saveSettings();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                });

                Thread.sleep(5 * 1000);

                MyLocalTon.getInstance().createFullnode(node2, true, true); //     add true to create wallet
                Utils.waitForBlockchainReady(node2);
                Utils.waitForNodeSynchronized(node2);
                saveSettings();

                Thread.sleep(10 * 1000);

                v = Utils.getConfig(settings.getGenesisNode());
                log.info("validation params {}", v);

                while (true) {
                    long electionId = new LiteClient().executeGetActiveElectionId(settings.getGenesisNode(), settings.getElectorSmcAddrHex());
                    log.info("ELECTION ID {} {}", electionId, Utils.toUTC(electionId));
                    if (electionId != 0) break;
                    Thread.sleep(10 * 1000);
                }

                long currentTime = Utils.getCurrentTimeSeconds();

                if ((currentTime > v.getStartElections()) && (currentTime < v.getEndElections())) {
                    log.info("Elections opened");
                    Utils.participate(settings.getGenesisNode(), v);
                    Utils.participate(node2, v);
                    //Utils.participate(node3, v);
                    //Utils.participate(node4, v);
                    //Utils.participate(node5, v);
                } else {
                    log.info("Elections closed");
                }

                Thread.sleep(2000);
                String stdout;
                while (true) {
                    stdout = new LiteClient().executeGetParticipantList(settings.getGenesisNode(), settings.getElectorSmcAddrHex());
                    if (LiteClientParser.parseRunMethodParticipantList(stdout).isEmpty()) {
                        break;
                    }
                    log.info("PARTICIPANTS {}, sleep 30sec", LiteClientParser.parseRunMethodParticipantList(stdout).size());
                    Thread.sleep(30 * 1000);
                }

                stdout = new LiteClient().executeGetCurrentValidators(settings.getGenesisNode());
                log.info(stdout);
                while (Long.parseLong(StringUtils.substringBetween(stdout, "total:", " ").trim()) != 2) {
                    stdout = new LiteClient().executeGetCurrentValidators(settings.getGenesisNode());
                    log.info("sleep 15 sec");
                    Thread.sleep(15 * 1000);
                }

                stdout = new LiteClient().executeGetPreviousValidators(settings.getGenesisNode());
                log.info(stdout);

                stdout = new LiteClient().executeGetNextValidators(settings.getGenesisNode());
                log.info(stdout);

                stdout = new LiteClient().executeGetCurrentValidators(settings.getGenesisNode());
                log.info(stdout);

                log.info("Up and running 2 new validators");

                while (true) {
                    long electionId = new LiteClient().executeGetActiveElectionId(settings.getGenesisNode(), settings.getElectorSmcAddrHex());
                    log.info("ELECTION ID {}, {}", electionId, Utils.toLocal(electionId));
                    //   if (electionId != 0) break;
                    Thread.sleep(15 * 1000);
                }

            } catch (Exception e) {
                log.error("ERROR, {}", e.getMessage());
            }
        });
    }

    public void drawElections() throws Exception {
        log.info("draw elections");

        ValidationParam v = Utils.getConfig(settings.getGenesisNode());
        log.info("validation params {}", v);

        double scaleFactor = (double) 200 / v.getValidationDuration();
        double scaleFactorElections = (double) (v.getEndElections() - v.getStartElections()) / v.getValidationDuration();// 20min/1h = 0.3
        double scaleFactorPause = (double) (v.getStartValidationCycle() - v.getEndElections()) / v.getValidationDuration();
        double scaleFactorHoldPeriod = (double) v.getHoldPeriod() / v.getValidationDuration();
        double scaleStartNextElections = (double) v.getStartElectionsBefore() / v.getValidationDuration();
        // assume duration of validation cycle is 1, then other ranges scaled down/up accordingly

        long space = 4;
        long validationWidth = 200;
        long electionsWidth = (long) ((v.getEndElections() - v.getStartElections()) * scaleFactor);
        long pauseWidth = (long) ((v.getStartValidationCycle() - v.getEndElections()) * scaleFactor);
        long holdStakeWidth = (long) (v.getHoldPeriod() * scaleFactor);

        // start X position of line 1 (very first elections)
        long startXElectionsLine1 = 20;
        long startXPauseLine1 = startXElectionsLine1 + electionsWidth + space;
        long startXValidationLine1 = startXPauseLine1 + pauseWidth + space;
        long startXHoldStakeLine1 = startXValidationLine1 + validationWidth + space;

        timeLine.setLayoutX((startXValidationLine1 + validationWidth));

        // start X position of line 2 (next elections)
        long startXElectionsLine2 = (long) ((startXValidationLine1 + validationWidth) - (scaleFactor * v.getStartElectionsBefore())); // validationCycleEndStart - electionsStartBefore
        long startXPauseLine2 = startXElectionsLine2 + +electionsWidth + space;
        long startXValidationLine2 = startXPauseLine2 + pauseWidth + space;
        long startXHoldStakeLine2 = startXValidationLine2 + validationWidth + space;

        // start X position of line 3 (next elections)
        long startXElectionsLine3 = startXElectionsLine2 + startXElectionsLine2 - startXElectionsLine1;
        long startXPauseLine3 = startXElectionsLine3 + +electionsWidth + space;
        long startXValidationLine3 = startXPauseLine3 + pauseWidth + space;
        long startXHoldStakeLine3 = startXValidationLine3 + validationWidth + space;

        log.info("scale {}, electionsWidth {}, pauseWidth {}, validationWidth {}, hostStakeWidth {}", scaleFactorElections, electionsWidth, pauseWidth, validationWidth, holdStakeWidth);

        electionsRange1.setMinWidth(electionsWidth);
        electionsRange2.setMinWidth(electionsWidth);
        electionsRange3.setMinWidth(electionsWidth);

        pauseRange1.setMinWidth(pauseWidth);
        pauseRange2.setMinWidth(pauseWidth);
        pauseRange3.setMinWidth(pauseWidth);

        validationRange1.setMinWidth(validationWidth);
        validationRange2.setMinWidth(validationWidth);
        validationRange3.setMinWidth(validationWidth);

        stakeHoldRange1.setMinWidth(holdStakeWidth);
        stakeHoldRange2.setMinWidth(holdStakeWidth);
        stakeHoldRange3.setMinWidth(holdStakeWidth);

        electionsRange1.setLayoutX(startXElectionsLine1);
        pauseRange1.setLayoutX(startXPauseLine1);
        validationRange1.setLayoutX(startXValidationLine1);
        stakeHoldRange1.setLayoutX(startXHoldStakeLine1);

        electionsRange2.setLayoutX(startXElectionsLine2);
        pauseRange2.setLayoutX(startXPauseLine2);
        validationRange2.setLayoutX(startXValidationLine2);
        stakeHoldRange2.setLayoutX(startXHoldStakeLine2);

        electionsRange3.setLayoutX(startXElectionsLine3);
        pauseRange3.setLayoutX(startXPauseLine3);
        validationRange3.setLayoutX(startXValidationLine3);
        stakeHoldRange3.setLayoutX(startXHoldStakeLine3);

        long electionDurationInSeconds = v.getEndElections() - v.getStartElections();
        String elections1Duration = DurationFormatUtils.formatDuration(java.time.Duration.ofSeconds(electionDurationInSeconds).toMillis(), "HH:mm:ss", true);
        String elections1ToolTip = String.format("Start: %s\nEnd: %s\nDuration: %s", Utils.toLocal(v.getStartElections()), Utils.toLocal(v.getEndElections()), elections1Duration);
        electionsRange1.setTooltip(new Tooltip(elections1ToolTip));

        long pauseDurationInSeconds = v.getStartValidationCycle() - v.getEndElections();
        String pause1Duration = DurationFormatUtils.formatDuration(java.time.Duration.ofSeconds(pauseDurationInSeconds).toMillis(), "HH:mm:ss", true);
        String pause1ToolTip = String.format("Start: %s\nEnd: %s\nDuration: %s", Utils.toLocal(v.getEndElections()), Utils.toLocal(v.getStartValidationCycle()), pause1Duration);
        pauseRange1.setTooltip(new Tooltip(pause1ToolTip));

        long validationDurationInSeconds = v.getEndValidationCycle() - v.getStartValidationCycle();
        String validation1Duration = DurationFormatUtils.formatDuration(java.time.Duration.ofSeconds(validationDurationInSeconds).toMillis(), "HH:mm:ss", true);
        String validation1ToolTip = String.format("Start: %s\nEnd: %s\nDuration: %s", Utils.toLocal(v.getStartValidationCycle()), Utils.toLocal(v.getEndValidationCycle()), validation1Duration);
        validationRange1.setTooltip(new Tooltip(validation1ToolTip));

        long stakeHoldDurationInSeconds = v.getHoldPeriod();
        String stakeHold1Duration = DurationFormatUtils.formatDuration(java.time.Duration.ofSeconds(stakeHoldDurationInSeconds).toMillis(), "HH:mm:ss", true);
        String stakeHold1ToolTip = String.format("Start: %s\nEnd: %s\nDuration: %s", Utils.toLocal(v.getStartValidationCycle()), Utils.toLocal(v.getEndValidationCycle()), stakeHold1Duration);
        stakeHoldRange1.setTooltip(new Tooltip(stakeHold1ToolTip));

        //add labels

    }

    private static final String SQUARE_BUBBLE = "M24 1h-24v16.981h4v5.019l7-5.019h13z";

    private Tooltip makeBubble(Tooltip tooltip) {
        tooltip.setStyle("-fx-font-size: 16px; -fx-shape: \"" + SQUARE_BUBBLE + "\";");
        tooltip.setAnchorLocation(PopupWindow.AnchorLocation.WINDOW_BOTTOM_LEFT);

        return tooltip;
    }
}
