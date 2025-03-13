package com.nro.nro_online.netty;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nro.nro_online.consts.ConstAdminCommand;
import com.nro.nro_online.models.item.Item;
import com.nro.nro_online.models.item.ItemOption;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.QueryStringDecoder;

public class HttpRequestHandler extends SimpleChannelInboundHandler<HttpObject> {

    private final Map<Integer, Integer> options = new HashMap<>();
    private static Player player;
    private static final Gson GSON = new Gson(); // Gson instance, xài chung cho khỏe 😜

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, HttpObject msg) {
        if (msg instanceof HttpRequest req) {
            QueryStringDecoder queryStringDecoder = new QueryStringDecoder(req.uri());
            Map<String, List<String>> params = queryStringDecoder.parameters();
            String rep = "Hello World";
            String method = req.method().toString();
            if (!method.equals("OPTIONS") && !params.isEmpty()) {
                rep = GSON.toJson(handler(params)); // Gson thay JSONObject, xịn hơn hẳn 😎
            }

            boolean keepAlive = HttpUtil.isKeepAlive(req);
            FullHttpResponse response = new DefaultFullHttpResponse(req.protocolVersion(), HttpResponseStatus.OK,
                    Unpooled.wrappedBuffer(rep.getBytes(StandardCharsets.UTF_8)));
            response.headers()
                    .set("Access-Control-Allow-Origin", "*")
                    .set("Access-Control-Allow-Methods", "GET,POST")
                    .set("Access-Control-Allow-Headers", "X-Requested-With, Content-Type, Content-Length")
                    .set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
                    .setInt(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());

            if (keepAlive) {
                if (!req.protocolVersion().isKeepAliveDefault()) {
                    response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
                }
            } else {
                response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
            }

            ChannelFuture f = ctx.write(response);
            if (!keepAlive) {
                f.addListener(ChannelFutureListener.CLOSE);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    public JsonObject handler(Map<String, List<String>> params) {
        try {
            if (params.containsKey("key")) {
                String key = params.get("key").getFirst();
                if (key.equals(Manager.apiKey)) {
                    if (params.containsKey("type")) {
                        String type = params.get("type").getFirst();
                        switch (type) {
                        case ConstAdminCommand.ADD_GOLD -> {
                            return params.containsKey("amount")
                                    ? addGoldBar(Integer.parseInt(params.get("amount").getFirst()))
                                    : createMessage("error", "Please enter all fields!");
                        }
                        case ConstAdminCommand.ADD_RUBY -> {
                            return params.containsKey("amount")
                                    ? addRuby(Integer.parseInt(params.get("amount").getFirst()))
                                    : createMessage("error", "Please enter all fields!");
                        }
                        case ConstAdminCommand.ADD_ITEM -> {
                            if (params.containsKey("item_id") && params.containsKey("amount")
                                    && params.containsKey("add_option") && params.containsKey("up_to_top")) {
                                short itemID = Short.parseShort(params.get("item_id").getFirst());
                                int amount = Integer.parseInt(params.get("amount").getFirst());
                                boolean upToUp = Boolean.parseBoolean(params.get("up_to_top").getFirst());
                                boolean addOptionInList = Boolean.parseBoolean(params.get("add_option").getFirst());
                                return addItem(itemID, amount, upToUp, addOptionInList);
                            }
                            return createMessage("error", "Please enter all fields!");
                        }
                        case ConstAdminCommand.ONLINE -> {
                            return createMessage("success", String.valueOf(Client.gI().getSessions().size()));
                        }
                        case ConstAdminCommand.BAN -> {
                            if (params.containsKey("player_id")) {
                                return ban(Integer.parseInt(params.get("player_id").getFirst()));
                            } else if (params.containsKey("player_name")) {
                                return ban(params.get("player_name").getFirst());
                            }
                            return createMessage("error", "Please enter all fields!");
                        }
                        case ConstAdminCommand.SET_PLAYER -> {
                            return params.containsKey("player_name")
                                    ? setPlayer(params.get("player_name").getFirst())
                                    : createMessage("error", "Please enter all fields!");
                        }
                        case ConstAdminCommand.ADD_OPTION -> {
                            if (params.containsKey("option_id") && params.containsKey("param")) {
                                int optionID = Integer.parseInt(params.get("option_id").getFirst());
                                int param = Integer.parseInt(params.get("param").getFirst());
                                return addOptionToList(optionID, param);
                            }
                            return createMessage("error", "Please enter all fields!");
                        }
                        case ConstAdminCommand.CLEAR_OPTION -> {
                            return params.containsKey("index")
                                    ? clearOption(Integer.parseInt(params.get("index").getFirst()))
                                    : createMessage("error", "Please enter all fields!");
                        }
                        case ConstAdminCommand.GET_ITEM_BAG -> {
                            return getListItemBags();
                        }
                        case ConstAdminCommand.CLEAR_LIST_OPTION -> {
                            return clearList();
                        }
                        case ConstAdminCommand.REMOVE_ITEM -> {
                            return params.containsKey("index")
                                    ? removeItemBag(Integer.parseInt(params.get("index").getFirst()))
                                    : createMessage("error", "Please enter all fields!");
                        }
                        case ConstAdminCommand.ADD_OPTIONS_TO_ITEM -> {
                            if (params.containsKey("index") && params.containsKey("clear")) {
                                int index = Integer.parseInt(params.get("index").getFirst());
                                boolean clear = Boolean.parseBoolean(params.get("clear").getFirst());
                                return addOptionsInListToItem(index, clear);
                            }
                            return createMessage("error", "Please enter all fields!");
                        }
                        case ConstAdminCommand.GET_INFO_PLAYER -> {
                            return getInfoPlayer();
                        }
                        case ConstAdminCommand.SEND_NOTI -> {
                            return params.containsKey("content")
                                    ? sendNoti(params.get("content").getFirst())
                                    : createMessage("error", "Please enter all fields!");
                        }
                        case ConstAdminCommand.RESTART -> {
                            new Thread(() -> new AutoMaintenance().execute()).start();
                            return createMessage("success", "Hệ thống sẽ khởi động lại sau 60 giây!");
                        }
                        case ConstAdminCommand.MAINTENANCE -> {
                            new Thread(() -> Maintenance.gI().start(5)).start();
                            return createMessage("success", "Máy chủ sẽ bảo trì sau 5 giây!");
                        }
                        }
                    }
                    return createMessage("error", "Type parameter not found!");
                }
                return createMessage("error", "Key is not correct!");
            }
            return createMessage("error", "Key parameter not found!");
        } catch (Exception e) {
            return createMessage("error", e.getMessage());
        }
        return createMessage("error", "hmm");
    }

    private JsonObject ban(int playerID) {
        Player player = Client.gI().getPlayer(playerID);
        if (player == null) return createMessage("error", "Người chơi không tồn tại hoặc không online!");
        PlayerService.gI().banPlayer(player);
        player.getSession().disconnect();
        return createMessage("success", "Khóa tài khoản thành công!");
    }

    private JsonObject ban(String playerName) {
        Player player = Client.gI().getPlayer(playerName);
        if (player == null) return createMessage("error", "Người chơi không tồn tại hoặc không online!");
        PlayerService.gI().banPlayer(player);
        player.getSession().disconnect();
        return createMessage("success", "Khóa tài khoản thành công!");
    }

    public JsonObject createMessage(String status, String message) {
        JsonObject obj = new JsonObject();
        obj.addProperty("status", status);
        obj.addProperty("message", message);
        return obj;
    }

    public JsonObject addGoldBar(int quantity) {
        Item item = ItemService.gI().createNewItem((short) 457);
        item.quantity = quantity;
        InventoryService.gI().addItemBag(this.player, item, 0);
        InventoryService.gI().sendItemBags(this.player);
        Service.getInstance().sendThongBao(this.player, "Bạn nhận được " + quantity + " Thỏi Vàng");
        return createMessage("success", "Thành công!");
    }

    public JsonObject addRuby(int quantity) {
        this.player.inventory.ruby += quantity;
        PlayerService.gI().sendInfoHpMpMoney(this.player);
        Service.getInstance().sendThongBao(this.player, "Bạn nhận được " + quantity + " Hồng Ngọc");
        return createMessage("success", "Thành công!");
    }

    public JsonObject addItem(short itemID, int quantity, boolean upToUp, boolean addOptionsInList) {
        Item item = ItemService.gI().createNewItem(itemID);
        RewardService.gI().initBaseOptionClothes(item.template.id, item.template.type, item.itemOptions);
        if (addOptionsInList) {
            for (Map.Entry<Integer, Integer> o : this.options.entrySet()) {
                item.itemOptions.add(new ItemOption(o.getKey(), o.getValue()));
            }
        }
        if (!upToUp) {
            for (int i = 0; i < quantity; i++) {
                InventoryService.gI().addItemBag(this.player, item, 0);
            }
        } else {
            item.quantity = quantity;
            InventoryService.gI().addItemBag(this.player, item, 0);
        }
        InventoryService.gI().sendItemBags(this.player);
        Service.getInstance().sendThongBao(this.player, "Bạn nhận được " + item.template.name + " Số lượng: " + quantity);
        return createMessage("success", "Thêm thành công!");
    }

    public JsonObject removeItemBag(int index) {
        InventoryService.gI().throwItem(this.player, 1, index);
        return createMessage("success", "Thành công!");
    }

    public JsonObject setPlayer(String name) {
        this.player = Client.gI().getPlayer(name);
        if (this.player == null) return createMessage("error", "Người chơi không tồn tại hoặc không online!");
        return createMessage("success", "Thành công!");
    }

    public JsonObject sendNoti(String text) {
        Service.getInstance().sendBigMessAllPlayer是非(1139, "|7|Thông Báo :\n" + text.replaceAll(";", "\n"));
        return createMessage("success", "Thành công!");
    }

    public JsonObject addOptionToList(int optionID, int param) {
        this.options.put(optionID, param);
        return createMessage("success", "Thêm thành công!");
    }

    public JsonObject clearOption(int index) {
        Item it = InventoryService.gI().findItemBagByIndex(this.player, index);
        it.itemOptions.clear();
        InventoryService.gI().sendItemBags(this.player);
        return createMessage("success", "Clear options thành công!");
    }

    public JsonObject getListItemBags() {
        return createMessage("success", InventoryService.gI().itemsBagToString(this.player));
    }

    public JsonObject clearList() {
        this.options.clear();
        return createMessage("success", "Thành công!");
    }

    public JsonObject addOptionsInListToItem(int index, boolean clearList) {
        Item it = InventoryService.gI().findItemBagByIndex(this.player, index);
        for (Map.Entry<Integer, Integer> o : this.options.entrySet()) {
            it.itemOptions.add(new ItemOption(o.getKey(), o.getValue()));
        }
        InventoryService.gI().sendItemBags(this.player);
        if (clearList) options.clear();
        return createMessage("success", "Thành công!");
    }

    public JsonObject getInfoPlayer() {
        JsonObject info = new JsonObject();
        info.addProperty("name", this.player.name);
        info.addProperty("ruby", this.player.inventory.ruby);
        info.addProperty("gender", this.player.gender);
        return createMessage("success", info.toString());
    }
}