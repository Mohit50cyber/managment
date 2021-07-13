package com.moglix.wms.service.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.moglix.wms.api.request.CreateBatchRequest;
import com.moglix.wms.api.request.CreatePacketRequest;
import com.moglix.wms.api.request.CreatePacketRequest.EMSOrderItem;
import com.moglix.wms.api.request.DeletePacketRequest;
import com.moglix.wms.api.request.EMSCancelPacketPackableQuantityRequest;
import com.moglix.wms.api.request.EMSPackableQuantityRequest;
import com.moglix.wms.api.request.GetInventoryForAllocatedQtyRequest;
import com.moglix.wms.api.request.GetPacketByIdRequest;
import com.moglix.wms.api.request.MSNListRequest;
import com.moglix.wms.api.request.PacketLotInfoRequest;
import com.moglix.wms.api.request.ProductInput;
import com.moglix.wms.api.request.ReturnBatchRequest.PacketQuantityMapping;
import com.moglix.wms.api.request.SearchPacketForPickupRequest;
import com.moglix.wms.api.response.BaseResponse;
import com.moglix.wms.api.response.CreatePacketResponse;
import com.moglix.wms.api.response.DeductInboundStorageResponse;
import com.moglix.wms.api.response.DeletePacketResponse;
import com.moglix.wms.api.response.GetInventoryForAllocatedQtyResponse;
import com.moglix.wms.api.response.GetPacketByIdResponse;
import com.moglix.wms.api.response.GetTPByEmsPacketIdResponse;
import com.moglix.wms.api.response.MSNListResponse;
import com.moglix.wms.api.response.PacketLotResponse;
import com.moglix.wms.api.response.SearchPacketForPickupResponse;
import com.moglix.wms.constants.BatchType;
import com.moglix.wms.constants.Constants;
import com.moglix.wms.constants.InventoryMovementType;
import com.moglix.wms.constants.InventoryTransactionType;
import com.moglix.wms.constants.PacketItemStatus;
import com.moglix.wms.constants.PacketStatus;
import com.moglix.wms.constants.PackingActions;
import com.moglix.wms.constants.SaleOrderAllocationStatus;
import com.moglix.wms.constants.SaleOrderStatus;
import com.moglix.wms.dto.InboundDTO;
import com.moglix.wms.dto.InventoryLocationDto.LocationDto;
import com.moglix.wms.dto.LotInfo;
import com.moglix.wms.dto.MSNListDTO;
import com.moglix.wms.dto.PacketDto;
import com.moglix.wms.dto.PacketItemDto;
import com.moglix.wms.dto.ReturnDetail;
import com.moglix.wms.dto.ReturnDetailDTO;
import com.moglix.wms.entities.Batch;
import com.moglix.wms.entities.InboundStorage;
import com.moglix.wms.entities.Packet;
import com.moglix.wms.entities.PacketItem;
import com.moglix.wms.entities.ProductInventory;
import com.moglix.wms.entities.ReturnPacket;
import com.moglix.wms.entities.ReturnPacketItem;
import com.moglix.wms.entities.SaleOrder;
import com.moglix.wms.entities.SaleOrderAllocation;
import com.moglix.wms.entities.SaleOrderAllocationHistory;
import com.moglix.wms.entities.Warehouse;
import com.moglix.wms.kafka.producer.KafkaInvoiceProducer;
import com.moglix.wms.mapper.ProductMapper;
import com.moglix.wms.repository.PacketRespository;
import com.moglix.wms.repository.ReturnPacketRepository;
import com.moglix.wms.repository.SaleOrderAllocationHistoryRepository;
import com.moglix.wms.repository.SaleOrderAllocationRepository;
import com.moglix.wms.service.IBatchService;
import com.moglix.wms.service.IInboundStorageService;
import com.moglix.wms.service.IPacketService;
import com.moglix.wms.service.IProductInventoryService;
import com.moglix.wms.service.ISaleOrderService;
import com.moglix.wms.util.DateUtil;
import com.moglix.wms.util.NumberUtil;
import com.moglix.wms.util.PaginationUtil;

@Service(value = "packetServiceImpl")
public class PacketServiceImpl implements IPacketService {

	Logger logger = LogManager.getLogger(PacketServiceImpl.class);

	@Autowired
	KafkaInvoiceProducer producer;

	@Autowired
	@Qualifier("saleOrderService")
	private ISaleOrderService saleOrderService;

	@Autowired
	private IProductInventoryService productInventoryService;

	@Autowired
	private PacketRespository packetRepository;

	@Autowired
	private InventoryServiceImpl inventoryService;

	@Autowired
	private SaleOrderAllocationHistoryRepository saleOrderAllocationHistoryRepository;

	@Autowired
	@Qualifier("batchService")
	private IBatchService batchService;

	@Autowired
	@Qualifier("inboundStorageServiceImpl")
	private IInboundStorageService inboundStorageServiceImpl;

	@Autowired
	private SaleOrderAllocationRepository saleOrderAllocationRepository;

	@Autowired
	private ReturnPacketRepository returnPacketRepository;

	@Autowired
	private WarehouseServiceImpl warehouseService;

	@Override
	public Packet upsert(Packet obj) {
		return packetRepository.save(obj);
	}

	@Override
	public Packet getById(Integer id) {
		return packetRepository.findById(id).orElse(null);
	}

	@Override
	public List<Packet> getByIdIn(List<Integer> ids) {
		return packetRepository.findByIdIn(ids);
	}

	@Override
	@Transactional
	public CreatePacketResponse createPacket(CreatePacketRequest request) {
		logger.info("Packet Creation started for ems Packet Id: " + request.getEmsPacketId());

		boolean isLotEnabled = false;
		// Create New Packet
		Packet packet = findByEmsPacketId(request.getEmsPacketId()).orElse(null);

		Set<PacketItem> packetItems;
		if (packet == null) {
			logger.trace("Packet Doesn't exist. Creating new one now");
			packet = new Packet();
			packet.setCancelled(false);
			packet.setEmsPacketId(request.getEmsPacketId());
			packet.setInvoiceNumber(request.getInvoiceNumber());
			packet.setStatus(PacketStatus.INVOICED);
			packet.setWarehouse(warehouseService.getById(request.getWarehouseId()));
			packetItems = new HashSet<>();
		} else {
			logger.trace("Packet Already exists");
			packet.setStatus(PacketStatus.INVOICED);
			packetItems = packet.getPacketItems();
		}
		Set<SaleOrderAllocationHistory> saleOrderAllocationHistories = new HashSet<>();

		// Transaction 1:
		// GET all order_allocations based ems_order_item_id order --> order_allocation
		// sorted on created.
		Map<Integer, Double> emsOrderQuantityMap = request.getEmsOrderItemIds().stream()
				.collect(Collectors.toMap(EMSOrderItem::getEmsOrderItemId, EMSOrderItem::getQuantity));
		logger.debug("EMSOrderQuantityMap: " + emsOrderQuantityMap);

		Set<Integer> emsOrderItemIds = emsOrderQuantityMap.keySet();

		logger.debug("Getting sale Orders for emsOrderItemIds: " + emsOrderItemIds);

		List<SaleOrder> saleOrders = saleOrderService.getOrdersfromOrderItemIds(emsOrderItemIds);
		logger.trace("Found " + saleOrders.size() + " saleOrders");
		logger.debug("saleOrders: " + saleOrders);
		if (saleOrders.isEmpty()) {
			logger.info("No order items found for packet: " + request.getEmsPacketId());
			return new CreatePacketResponse("Error in creating packets. No eligible orders found for creating packets",
					false, HttpStatus.OK.value());
		}

		// Total Quantity of items in a packet
		Double packetQuantity = 0.0d;
		logger.trace("Packet Quantity Initialised with 0");

		// total unique products in a packet
		Set<String> productMsnSet = new HashSet<>();
		logger.trace("Packet Msn Set Initialised with Empty Set");
		for (SaleOrder saleOrder : saleOrders) {
			logger.trace("Checking if Sale Order" + saleOrder.getEmsOrderItemId() + " has correct Value");
			logger.info("Checking if quantity to be packed for all orders are valid.");
			logger.debug("Sale Order Allocated Quantity: " + NumberUtil.round4(saleOrder.getAllocatedQuantity()));
			logger.debug("Sale Order Ordered Quantity: "
					+ NumberUtil.round4(emsOrderQuantityMap.get(saleOrder.getEmsOrderItemId())));

			if (NumberUtil.round4(saleOrder.getAllocatedQuantity()) < NumberUtil
					.round4(emsOrderQuantityMap.get(saleOrder.getEmsOrderItemId()))) {
				logger.info("Invalid quantity found for order: " + saleOrder.getEmsOrderId() + ". "
						+ "Error in creating packets. Available Packable Quantity is: "
						+ NumberUtil.round4(saleOrder.getAllocatedQuantity())
						+ " Total Requested Packable Quantity is: "
						+ NumberUtil.round4(emsOrderQuantityMap.get(saleOrder.getEmsOrderItemId())));

				return new CreatePacketResponse(
						"Error in creating packets. Available Packable Quantity is: " + saleOrder.getAllocatedQuantity()
								+ " Total Requested Packable Quantity is: "
								+ (saleOrder.getPackedQuantity()
										+ emsOrderQuantityMap.get(saleOrder.getEmsOrderItemId())),
						false, HttpStatus.OK.value());

			}
			List<SaleOrderAllocation> allocations = saleOrderAllocationRepository
					.getSaleOrderAllocationBySaleOrderIdAndStatus(saleOrder.getId(),
							SaleOrderAllocationStatus.ALLOCATED);
			logger.trace("Found " + allocations.size() + " allocations for order: " + saleOrder.getEmsOrderItemId());

			logger.debug(allocations);

			if (allocations.isEmpty()) {
				logger.info("Allocations are empty for saleOrder: " + saleOrder.getEmsOrderItemId());
				return new CreatePacketResponse("Error in creating packets. Inventory transferred from order: "
						+ saleOrder.getEmsOrderItemId() + " to another order ", false, HttpStatus.OK.value());
			}
		}

		for (SaleOrder saleOrder : saleOrders) {
			logger.info("Allocating quantities for order: " + saleOrder.getEmsOrderItemId());
			Double packingQuantity = NumberUtil.round4(emsOrderQuantityMap.get(saleOrder.getEmsOrderItemId()));
			logger.debug("Packing Quantity: " + packingQuantity);
			Double totalPackedQuantity = 0.0000d;
			logger.trace("Initialised totalPacked Quantity as 0");
			List<SaleOrderAllocation> allocations = saleOrderAllocationRepository
					.getSaleOrderAllocationBySaleOrderIdAndStatus(saleOrder.getId(),
							SaleOrderAllocationStatus.ALLOCATED);
			logger.trace("Found " + allocations.size() + "allocations");
			for (SaleOrderAllocation allocation : allocations) {
				logger.trace("Entering allocation loop");
				logger.debug("Packing Quantity: " + packingQuantity);
				logger.debug("Allocation available quantity: " + allocation.getAvailableQuantity());
				if (packingQuantity != 0 && allocation.getAvailableQuantity() != 0) {
					logger.trace("Inside Create Packet Item Block");
					PacketItem packetItem = new PacketItem();
					SaleOrderAllocationHistory saleOrderHistory = new SaleOrderAllocationHistory();
					// Transaction 2:
					// Deduct allocated quantity and add packed quantity
					Double packedQuantity = NumberUtil
							.round4(Math.min(packingQuantity, allocation.getAvailableQuantity()));
					logger.debug("Packed Quantity: " + packedQuantity);

					logger.debug("Setting allocation Available Quantity to "
							+ (NumberUtil.round4(allocation.getAvailableQuantity() - packedQuantity)));

					allocation.setAvailableQuantity(
							NumberUtil.round4(allocation.getAvailableQuantity() - packedQuantity));

					logger.debug("Setting allocation Packed Quantity to "
							+ (NumberUtil.round4(allocation.getPackedQuantity() + packedQuantity)));
					allocation.setPackedQuantity(NumberUtil.round4(allocation.getPackedQuantity() + packedQuantity));

					logger.debug("Setting allocation id: " + allocation.getId());

					packetItem.setSaleOrderAllocation(allocation);

					totalPackedQuantity = NumberUtil.round4(totalPackedQuantity + packedQuantity);
					logger.debug("Total Packed Quantity: " + totalPackedQuantity);

					logger.debug("Setting Packet Item Quantity to: " + (NumberUtil.round4(packedQuantity)));
					packetItem.setQuantity(NumberUtil.round4(packedQuantity));

					logger.debug("Setting InboundStorage to: " + allocation.getInboundStorage().getId());
					packetItem.setInboundStorage(allocation.getInboundStorage());

					logger.debug("Setting PacketId: " + packet.getId());
					packetItem.setPacket(packet);

					logger.debug("Setting SaleOrderId: " + saleOrder.getId());
					packetItem.setSaleOrder(saleOrder);

					saleOrderHistory.setSaleOrder(saleOrder);
					saleOrderHistory.setQuantity(NumberUtil.round4(packedQuantity));
					saleOrderHistory.setAction(PackingActions.PACKED.toString());
					if (allocation.getPacketItems() != null) {
						allocation.getPacketItems().add(packetItem);
					} else {
						Set<PacketItem> tempPacketItems = new HashSet<>();
						tempPacketItems.add(packetItem);
						allocation.setPacketItems(tempPacketItems);
					}
					logger.trace("Adding packetItem to packetItem List");
					packetItems.add(packetItem);
					logger.debug("packet item list has size: " + packetItems.size());

					productMsnSet.add(saleOrder.getProduct().getProductMsn());
					logger.debug("Product Msn Set has " + productMsnSet.size() + " Size");

					saleOrderAllocationHistories.add(saleOrderHistory);
					packingQuantity = NumberUtil.round4(packingQuantity - packedQuantity);
					logger.debug("packingQuantity at the end of loop: " + packingQuantity);
				}
				

				// Transaction 5:
				// Set order status as fulfilled
				if (saleOrder.getProduct().getLotManagementEnabled() != null
						&& saleOrder.getProduct().getLotManagementEnabled() && !isLotEnabled
						&& StringUtils.isNotBlank(allocation.getInboundStorage().getLotNumber())) {
					isLotEnabled = true;
				}
				
				if (NumberUtil.round4(saleOrder.getPackedQuantity()) + totalPackedQuantity >= NumberUtil
						.round4(saleOrder.getOrderedQuantity())) {

					logger.info("Order Fulfilled for orderId: " + saleOrder.getEmsOrderId());
					saleOrder.setStatus(SaleOrderStatus.FULFILLED);
					// Add entry in sale order allocation history table.
					SaleOrderAllocationHistory saleOrderhistory = new SaleOrderAllocationHistory();
					saleOrderhistory.setSaleOrder(saleOrder);
					saleOrderhistory.setAction(SaleOrderStatus.FULFILLED.toString());
					saleOrderAllocationHistories.add(saleOrderhistory);
					break;
				}
			}
			
			if(packetItems.isEmpty()) {
				logger.error("Available Quantity is less than packable quantity" + packingQuantity);
				return new CreatePacketResponse("Available Quantity is less than packable quantity", false, HttpStatus.BAD_REQUEST.value());
			}
			
			packetQuantity = NumberUtil.round4(packetQuantity + totalPackedQuantity);
			logger.debug("packet Quantity at the end of loop: " + packetQuantity);
			// Transaction 3:
			// Deduct Allocated Quantity and add packed Quantity
			logger.debug("Setting packed Quantity for saleOrder: " + saleOrder.getEmsOrderItemId() + "as "
					+ NumberUtil.round4(saleOrder.getPackedQuantity() + totalPackedQuantity));
			saleOrder.setPackedQuantity(NumberUtil.round4(saleOrder.getPackedQuantity() + totalPackedQuantity));

			logger.debug("Setting allocated Quantity for saleOrder: " + saleOrder.getEmsOrderItemId() + "as "
					+ NumberUtil.round4(saleOrder.getAllocatedQuantity() - totalPackedQuantity));
			saleOrder.setAllocatedQuantity(NumberUtil.round4(saleOrder.getAllocatedQuantity() - totalPackedQuantity));
			// Transaction 7:
			// Deduct Allocated inventory in product_inventory table when an item is packed.
			// Moved to deductInboundStorageAPI
		}

		logger.debug("Set PacketItems for packet " + request.getEmsPacketId() + ": " + packetItems);
		packet.setPacketItems(packetItems);
		
		packet.setIsLotEnabled(isLotEnabled);

		logger.debug("Updating MsnSet for Packet: " + productMsnSet);
		packet.setMsnCount(productMsnSet.size());

		logger.debug("Updation totalQuantity of packet: " + packetQuantity);
		packet.setTotalQuantity(packetQuantity);

		packet.setWarehouse(saleOrders.get(0).getWarehouse());

		// Transaction 4:
		// Add allocation histories to table
		logger.info("Updating order Allocation History table for packet: " + request.getEmsPacketId());
		saleOrderAllocationHistoryRepository.saveAll(saleOrderAllocationHistories);
		// Transaction 6:

		logger.info("Creating packet for packet with emsPacketId: " + request.getEmsPacketId());
		packetRepository.save(packet);

		// Transaction 8:
		// Update EMS through API.
		logger.info("Contacting ems for new updated packable quantity for packet: " + request.getEmsPacketId());
		for (Map.Entry<Integer, Double> entry : saleOrders.stream()
				.collect(Collectors.toMap(SaleOrder::getEmsOrderItemId, SaleOrder::getAllocatedQuantity)).entrySet()) {
			RestTemplate restTemplate = new RestTemplate();
			EMSPackableQuantityRequest emsRequest = new EMSPackableQuantityRequest(entry.getKey(), entry.getValue(), "WMS");
			restTemplate.postForEntity(Constants.EMS_PACKABLE_QUANTITY_API, emsRequest, BaseResponse.class);
		}

		logger.info("Packet successfully created for ID: " + request.getEmsPacketId());

		logger.info("Sending packet to topic with invoice number: " + request.getInvoiceNumber());
		// sendDataToTopic(request);
		logger.info("Packet Successfully sent to topic: " + request.getInvoiceNumber());

		return new CreatePacketResponse("Packet Created", true, HttpStatus.OK.value());
	}

	private void sendDataToTopic(CreatePacketRequest request) {
		producer.send(request);
	}

	@Override
	@Transactional
	public DeletePacketResponse deletePacket(DeletePacketRequest request) {

		logger.info("Recieved request to delete packet: " + request.getEmsPacketId());
		Set<SaleOrderAllocationHistory> saleOrderAllocationHistories = new HashSet<>();
		Integer emsPacketId = request.getEmsPacketId();
		Packet packet = findByEmsPacketIdAndStatusNot(request.getEmsPacketId(), PacketStatus.CANCELLED).orElse(null);

		if (packet == null) {
			logger.info("Cannot Delete Packet because no packet found with EMS ID: " + emsPacketId);
			return new DeletePacketResponse("Cannot Delete Packet because no packet found with EMS ID: " + emsPacketId,
					false, HttpStatus.OK.value());
		}

		packet.setStatus(PacketStatus.CANCELLED);
		packet.setCancelled(true);

		logger.info("Deallocating packed quantity");
		Map<Integer, Double> emsOrderItemIdPackableQuantityMap = new HashMap<>();
		for (PacketItem packetItem : packet.getPacketItems()) {
			packetItem.setStatus(PacketItemStatus.CANCELLED);
			SaleOrderAllocationHistory history = new SaleOrderAllocationHistory();
			history.setQuantity(packetItem.getQuantity());
			history.setSaleOrder(packetItem.getSaleOrder());
			history.setAction(PackingActions.UNPACKED.toString());
			saleOrderAllocationHistories.add(history);
			SaleOrder order = packetItem.getSaleOrder();
			SaleOrderAllocation allocation = packetItem.getSaleOrderAllocation();
			if (allocation != null) {
				allocation.setAvailableQuantity(
						NumberUtil.round4(allocation.getAvailableQuantity() + packetItem.getQuantity()));
				allocation.setPackedQuantity(
						NumberUtil.round4(allocation.getPackedQuantity() - packetItem.getQuantity()));
				allocation.setStatus(SaleOrderAllocationStatus.ALLOCATED);
				saleOrderAllocationRepository.save(allocation);
			}
			order.setPackedQuantity(order.getPackedQuantity() - packetItem.getQuantity());
			order.setAllocatedQuantity(order.getAllocatedQuantity() + packetItem.getQuantity());
			if (order.getOrderedQuantity() > order.getPackedQuantity()
					&& !order.getStatus().name().equals(SaleOrderStatus.OPEN.name())) {
				order.setStatus(SaleOrderStatus.OPEN);
			}
			saleOrderService.upsert(order);

			emsOrderItemIdPackableQuantityMap.put(order.getEmsOrderItemId(), order.getAllocatedQuantity());
		}

		// Update EMS through API.
		logger.info("Contacting ems for new updated packable quantity for packet: " + request.getEmsPacketId());
		for (Map.Entry<Integer, Double> entry : emsOrderItemIdPackableQuantityMap.entrySet()) {
			RestTemplate restTemplate = new RestTemplate();
			EMSCancelPacketPackableQuantityRequest emsRequest = new EMSCancelPacketPackableQuantityRequest(entry.getKey(), entry.getValue(), "WMS", packet.getInvoiceNumber());
			//restTemplate.postForEntity(Constants.EMS_CANCEL_PACKET_PACKABLE_QUANTITY_API, emsRequest, BaseResponse.class);
			URI url = null;
			try {
				url = new URI(Constants.EMS_CANCEL_PACKET_PACKABLE_QUANTITY_API);
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			restTemplate.postForEntity(url, emsRequest, BaseResponse.class);

			// Possible alternative with better error handling
			// restTemplate.exchange(Constants.EMS_PACKABLE_QUANTITY_API, HttpMethod.POST,
			// new HttpEntity<EMSPackableQuantityRequest>(emsRequest), BaseResponse.class);
		}

		saleOrderAllocationHistoryRepository.saveAll(saleOrderAllocationHistories);

		packetRepository.save(packet);

		logger.info("Packet successfully deleted for ems packet Id: " + packet.getEmsPacketId());
		return new DeletePacketResponse("Packet Successfully deleted", true, HttpStatus.OK.value());
	}

	public void inboundPacket(Packet packet) {
		Map<Batch, List<PacketItem>> map = packet.getPacketItems().stream()
				.collect(Collectors.groupingBy(e -> e.getInboundStorage().getInbound().getBatch()));
		for (Map.Entry<Batch, List<PacketItem>> entry : map.entrySet()) {
			CreateBatchRequest request = new CreateBatchRequest();
			List<ProductInput> productInput = new ArrayList<>();
			Batch batch = entry.getKey();

			request.setSupplierId(batch.getSupplierId());
			request.setSupplierName(batch.getSupplierName());
			request.setBatchType(BatchType.PACKET_CANCELLED);
			request.setRefNo("" + packet.getEmsPacketId() + batch.getId());
			request.setWarehouseId(batch.getWarehouseId());
			request.setWarehouseName(batch.getWarehouseName());
			request.setInboundedBy(batch.getInboundedBy());
			request.setPurchaseDate(DateUtil.getCurrentDateTime());
			for (PacketItem item : entry.getValue()) {
				ProductInput input = ProductMapper.createInputFromEntity(item.getInboundStorage().getProduct());
				input.setQuantity(item.getQuantity());
				input.setSupplierPoId(item.getInboundStorage().getInbound().getSupplierPoId());
				input.setSupplierPoItemId(item.getInboundStorage().getInbound().getSupplierPoItemId());
				productInput.add(input);
			}
			request.setProducts(productInput);
			batchService.createBatch(request);
		}

	}

	@Transactional
	public void returnPacket(Packet packet, List<PacketQuantityMapping> mapping, String customerName,
			Integer emsReturnId) {
		Set<ReturnPacketItem> returnPacketItems = new HashSet<>();

		ReturnPacket returnPacket = new ReturnPacket();
		returnPacket.setEmsPacketId(packet.getEmsPacketId());
		returnPacket.setInvoiceNumber(packet.getInvoiceNumber());
		returnPacket.setMsnCount(mapping.stream().distinct().count());
		if(packet.getId() != null) {
		returnPacket.setPacket(packet);			
		}
		returnPacket.setWarehouse(packet.getWarehouse());
		returnPacket.setStatus(PacketStatus.RETURNED);
		returnPacket.setCustomerName(customerName);
		returnPacket.setEmsReturnId(emsReturnId);
		returnPacket.setTotalQuantity(mapping.stream().mapToDouble(PacketQuantityMapping::getQuantity).sum());
		for (PacketQuantityMapping item : mapping) {
			ReturnPacketItem returnItem = new ReturnPacketItem();
			returnItem.setReturnPacket(returnPacket);
			returnItem.setQuantity(item.getQuantity());
			returnItem.setProductMsn(item.getProductMsn());
			returnItem.setEmsOrderItemId(item.getOrderItemId());
			returnItem.setEmsReturnItemId(item.getEmsReturnItemId());
			returnPacketItems.add(returnItem);
		}

		returnPacket.setReturnPacketItems(returnPacketItems);
		returnPacketRepository.save(returnPacket);
	}

	public Optional<Packet> findByPacketId(Integer packetId) {
		return packetRepository.findById(packetId);
	}

	public Optional<Packet> findByEmsPacketIdAndStatusNot(Integer emsPacketId, PacketStatus status) {
		return packetRepository.findByEmsPacketIdAndStatusNot(emsPacketId, status);
	}

	@Override
	public Optional<Packet> findByInvoiceNumberAndStatusNot(String invoiceNumber, PacketStatus status) {
		return packetRepository.findByInvoiceNumberAndStatusNot(invoiceNumber, status);
	}

	@Override
	public Optional<Packet> findByEmsPacketId(Integer emsPacketId) {
		return packetRepository.findByEmsPacketId(emsPacketId);
	}

	@Override
	@Transactional
	public SearchPacketForPickupResponse searchPacketForPickup(SearchPacketForPickupRequest request, Pageable page) {
		logger.info("Search Packet for Pickup Service Started");
		SearchPacketForPickupResponse response;
		Page<Packet> packets;

		if (request.getStatus() != null && request.getInvoiceNumber() == null) {
			logger.info("Search Packet for Pickup Service Started" + request.toString());
			PacketStatus status = request.getStatus();
			packets = packetRepository.findByWarehouseIdAndStatusOrderByIdAsc(request.getWarehouseId(), status, page);

		} else if (request.getStatus() != null && request.getInvoiceNumber() != null) {
			logger.info("Search Packet for Pickup Service Started" + request.toString());
			PacketStatus status = request.getStatus();
			packets = packetRepository.findByWarehouseIdAndInvoiceNumberAndStatusOrderByIdAsc(request.getWarehouseId(),
					request.getInvoiceNumber(), status, page);

		} else if (request.getInvoiceNumber() == null && request.getStatus() == null) {
			packets = packetRepository.findByWarehouseIdAndStatusNotOrderByIdAsc(request.getWarehouseId(),
					PacketStatus.CANCELLED, page);
		} else {
			packets = packetRepository.findByWarehouseIdAndInvoiceNumberAndStatusNotOrderByIdAsc(
					request.getWarehouseId(), request.getInvoiceNumber(), PacketStatus.CANCELLED, page);
		}

		List<PacketDto> packetDtos = new ArrayList<>();
		if (!packets.getContent().isEmpty()) {
			for (Packet packet : packets) {
				packetDtos.add(new PacketDto(packet));
			}
			response = (SearchPacketForPickupResponse) PaginationUtil.setPaginationParams(packets,
					new SearchPacketForPickupResponse("Packets found : " + packets.getTotalElements(), true,
							HttpStatus.OK.value()));
			response.setPackets(packetDtos);
		} else {
			response = new SearchPacketForPickupResponse(
					"No Packet found for warehouse id: " + request.getWarehouseId(), true, HttpStatus.OK.value());
		}
		logger.info("Search Packet for Pickup Service Ended");
		return response;
	}

	@Override
	@Transactional
	public DeductInboundStorageResponse deductInboundStorages(Integer emsPacketId) {
		
		String uuid = UUID.randomUUID().toString();

		Packet packet = packetRepository.findByEmsPacketId(emsPacketId).orElse(null);

		if (packet == null) {
			logger.info("No Packet found with Pickuplist Done for emsPacketId: " + emsPacketId + " : " + uuid);
			return new DeductInboundStorageResponse(
					"Cannot Deduct InboundStorage because no packet found for emsPacketId: " + emsPacketId, false, 200);
		}
		else if(packet.getIsLotEnabled() != null && packet.getIsLotEnabled() && !packet.getStatus().equals(PacketStatus.SCANNED)) {
			if(packet.getStatus().equals(PacketStatus.PICKUPLIST_DONE)) {
				return new DeductInboundStorageResponse(
						"Please scan the lot number in wms", false, 200);
			}
			else {
				return new DeductInboundStorageResponse(
						"Please create the pickuplist in wms", false, 200);
			}
		} 
		else if ((packet.getIsLotEnabled() != null && !packet.getIsLotEnabled()
				&& !packet.getStatus().equals(PacketStatus.PICKUPLIST_DONE))
				|| (packet.getIsLotEnabled() == null && !packet.getStatus().equals(PacketStatus.PICKUPLIST_DONE))) {
			return new DeductInboundStorageResponse("Please create the pickuplist in wms" + packet.getStatus(), false,200);
		}

		Warehouse warehouse = packet.getWarehouse();

		Set<PacketItem> packetItems = packet.getPacketItems();
		logger.info("Found " + packetItems.size() + "packetItems" + " : " + uuid);

		List<InboundStorage> inboundStorages = new ArrayList<>();
		Map<String, Double> prevProductQuantityMap = new HashMap<>();
		Map<String, Double> currentProductQuantityMap = new HashMap<>();
		// Map<Integer, Double> saleOrderAllocationShippedQuantityMap = new HashMap<>();

		for (PacketItem item : packetItems) {
			logger.trace("Inside packetItems loop" + " : " + uuid);
			InboundStorage storage = item.getInboundStorage();

			logger.debug("InboundStorage Id: " + storage.getId() + " : " + uuid);

			logger.debug("Updating allocated quantity of storage to :: " + (storage.getAllocatedQuantity() - item.getQuantity()) + " : " + uuid);
			
			if(NumberUtil.round4(storage.getAllocatedQuantity() - item.getQuantity()) < 0) {
				storage.setAllocatedQuantity(0.0);
				logger.error("Allocated quantity should not be negative in InboundStorage :: ProductMsn ::[" +  item.getSaleOrder().getProduct().getProductMsn() +"]");
			}
			else {
				storage.setAllocatedQuantity(storage.getAllocatedQuantity() - item.getQuantity());
			}

			logger.debug("Updating quantity of storage to: " + (storage.getQuantity() - item.getQuantity()) + " : " + uuid);
			
			if(NumberUtil.round4(storage.getQuantity() - item.getQuantity()) < 0) {
				storage.setQuantity(0.0);
				logger.error("Quantity should not be negative in InboundStorage :: ProductMsn ::[" +  item.getSaleOrder().getProduct().getProductMsn() +"]");
			}
			else {
				storage.setQuantity(storage.getQuantity() - item.getQuantity());
			}

			inboundStorages.add(storage);
			String productMsn = item.getSaleOrder().getProduct().getProductMsn();
			prevProductQuantityMap.compute(productMsn,
					(k, v) -> v == null ? productInventoryService
							.getByWarehouseIdAndProductId(warehouse.getId(), item.getSaleOrder().getProduct().getId())
							.getCurrentQuantity() : v);
			currentProductQuantityMap.compute(productMsn,
					(k, v) -> v == null ? item.getQuantity() : NumberUtil.round4(v + item.getQuantity()));
			// saleOrderAllocationShippedQuantityMap.compute(item.getSaleOrderAllocation().getId(),
			// (k,v) -> v == null ? item.getQuantity() : NumberUtil.round4(v +
			// item.getQuantity()));
		}

		logger.debug("PrevProductQuantityMap: " + prevProductQuantityMap + " : " + uuid);

		logger.debug("CurrentProductQuantityMap:" + currentProductQuantityMap + " : " + uuid);

		for (Map.Entry<String, Double> entry : prevProductQuantityMap.entrySet()) {
			inventoryService.saveInventoryHistory(warehouse, entry.getKey(), InventoryTransactionType.PACKET_CREATED,
					InventoryMovementType.INVENTORY_OUT, String.valueOf(packet.getEmsPacketId()), entry.getValue(),
					(NumberUtil.round4(entry.getValue() - currentProductQuantityMap.get(entry.getKey()))));
		}

		Map<SaleOrder, Double> saleOrderQuantity = packetItems.stream().collect(
				Collectors.groupingBy(PacketItem::getSaleOrder, Collectors.summingDouble(PacketItem::getQuantity)));

		logger.debug("SaleOrderQuantityMap: " + saleOrderQuantity + " : " + uuid);

		// update sale order allocation shipped quantity
		// saleOrderService.updateSaleOrderAllocationShippedQuantity(saleOrderAllocationShippedQuantityMap);
		logger.debug("Checking for All Quantity Availaiblty" + uuid);
		for (Map.Entry<SaleOrder, Double> entry : saleOrderQuantity.entrySet()) {
			
			boolean  status= inventoryService.checkAllInventoryAvailable(entry.getKey().getWarehouse().getId(),
					entry.getKey().getProduct().getId(), NumberUtil.round4(entry.getValue()));
			if(!status){
				logger.debug("Inventory Not available for Item Ref "+entry.getKey().getItemRef());
				return new DeductInboundStorageResponse("Inventory Not available for Item Ref "+entry.getKey().getItemRef(), false, 200);
			}
		}

		for (Map.Entry<SaleOrder, Double> entry : saleOrderQuantity.entrySet()) {
			
			inventoryService.deductAllocatedInventory(entry.getKey().getWarehouse().getId(),
					entry.getKey().getProduct().getId(), NumberUtil.round4(entry.getValue()));
			
		}
		
		packet.setStatus(PacketStatus.SHIPPED);

		packetRepository.save(packet);

		inboundStorageServiceImpl.saveAll(inboundStorages);

		return new DeductInboundStorageResponse("Inbound Storages Successfully Deducted", true, 200);
	}

	@Override
	@Transactional
	public GetPacketByIdResponse getPacketById(GetPacketByIdRequest request) {
		GetPacketByIdResponse response = new GetPacketByIdResponse();
		Packet packet = getById(request.getPacketId());
		if (packet == null) {
			response.setMessage("No Packet Details found for id: " + request.getPacketId());
		} else {
			PacketDto packetDto = new PacketDto(packet);
			for (PacketItem item : packet.getPacketItems()) {
				packetDto.getPacketItems().add(new PacketItemDto(item));
			}
			response.setPacket(packetDto);
			response.setMessage("Packet Details found");
			response.setStatus(true);
		}
		return response;
	}

	@Override
	@Transactional
	public GetTPByEmsPacketIdResponse getTransferPriceByPacketId(Integer emsPacketId) {
		GetTPByEmsPacketIdResponse response = new GetTPByEmsPacketIdResponse();
		Packet packet = packetRepository.findByEmsPacketId(emsPacketId).orElse(null);

		if (packet == null) {
			response.setMessage("No Packet Details found for id: " + emsPacketId);
			response.setStatus(false);
		} else {
			Map<Integer, InboundDTO> tpMap = packet.getPacketItems().stream()
					.collect(Collectors.toMap(e -> e.getSaleOrder().getEmsOrderItemId(),
							e -> new InboundDTO(e.getInboundStorage().getInbound()), (e1, e2) -> {
								e1.setPurchasePrice(((e1.getPurchasePrice() * e1.getQuantity())
										+ (e2.getPurchasePrice() * e2.getQuantity()))
										/ (e1.getQuantity() + e2.getQuantity()));
								return e1;
							}));

			for (Map.Entry<Integer, InboundDTO> entry : tpMap.entrySet()) {
				GetTPByEmsPacketIdResponse.OrderItemIdTPMapping mapping = new GetTPByEmsPacketIdResponse.OrderItemIdTPMapping();
				mapping.setEmsOrderItemId(entry.getKey());
				mapping.setTransferPrice(entry.getValue().getPurchasePrice());
				response.getTransferPrices().add(mapping);
			}

			response.setMessage("Packet Details found");
			response.setStatus(true);
		}
		response.setCode(HttpStatus.OK.value());
		return response;
	}

	@Override
	public BaseResponse markShipped(Integer emsPacketId) {
		logger.info("mark shipping start for ems packet id :" + emsPacketId);
		BaseResponse response = new BaseResponse();
		Packet packet = packetRepository.findByEmsPacketId(emsPacketId).orElse(null);
		if (packet == null) {
			response.setMessage("No Packet Details found for id: " + emsPacketId);
			response.setStatus(false);
		} else {
			if(packet.getIsLotEnabled() != null && packet.getIsLotEnabled()) {
				if(packet.getStatus().equals(PacketStatus.SCANNED)) {
					packet.setStatus(PacketStatus.SHIPPED);
					packetRepository.save(packet);
					response.setMessage("Packet marked successfully for id: " + emsPacketId);
					response.setStatus(true);
				}else {
					response.setMessage("Packet has lot management enabled but not in SCANNED state for id: " + emsPacketId);
					response.setStatus(false);
				}
				
			}else {
				if (packet.getStatus().equals(PacketStatus.PICKUPLIST_DONE)
						|| packet.getStatus().equals(PacketStatus.INVOICED)) {
					packet.setStatus(PacketStatus.SHIPPED);
					packetRepository.save(packet);
					response.setMessage("Packet marked successfully for id: " + emsPacketId);
					response.setStatus(true);
				} else {
					response.setMessage("Packet already marked shipped for id: " + emsPacketId);
					response.setStatus(false);
				}
			}
			
		}
		response.setCode(HttpStatus.OK.value());
		logger.info("mark shipping done for ems packet id :" + emsPacketId);
		return response;
	}

	@Override
	public Set<String> findUnshippedOrders() {
		List<ProductInventory> inventories = productInventoryService.findByAllocatedQuantityGreaterThan(0.0d);
		Set<String> invoiceNumbers = new HashSet<>();
		for (ProductInventory inventory : inventories) {

			GetInventoryForAllocatedQtyRequest request = new GetInventoryForAllocatedQtyRequest();

			request.setProductId(inventory.getProduct().getId());
			request.setWarehouseId(inventory.getWarehouse().getId());
			GetInventoryForAllocatedQtyResponse response = saleOrderService
					.getProductInventoryAvailabilityInfo(request);
			if (response.getInventoryLocation() != null) {
				List<LocationDto> locations = response.getInventoryLocation().getLocations();
				if (!CollectionUtils.isEmpty(locations)) {
					for (LocationDto location : locations) {
						if (StringUtils.isNotBlank(location.getInvoiceNumber())) {
							invoiceNumbers.add(location.getInvoiceNumber());
						}
					}
				}
			}
		}

		return invoiceNumbers;
	}

	@Override
	@Transactional
	public List<ReturnDetail> getReturnPacketDetails(String invoiceNumber) {
		List<ReturnDetailDTO> returnDetailResultSet = packetRepository.getReturnPacketDetails(invoiceNumber);
		
		List<ReturnDetail> returnDetails = new ArrayList<>();

		for (ReturnDetailDTO returnDetailDTO : returnDetailResultSet) {
			ReturnPacket returnPacket = returnPacketRepository.findByEmsReturnId(returnDetailDTO.getEmsReturnId())
					.orElse(null);

			if (returnPacket != null) {
				Integer emsReturnItemId = returnPacket.getReturnPacketItems().stream()
						.filter(e -> e.getProductMsn().equals(returnDetailDTO.getProductMsn()))
						.findFirst().get().getEmsReturnItemId();
				
				ReturnDetail returnDetail = new ReturnDetail();
				
				returnDetail.setDebitDoneQuantity(returnDetailDTO.getDebitDoneQuantity());
				
				returnDetail.setEmsReturnId(returnDetailDTO.getEmsReturnId());
				
				returnDetail.setEmsReturnItemId(emsReturnItemId);
				
				returnDetail.setProductMsn(returnDetailDTO.getProductMsn());
				
				returnDetail.setProductName(returnDetailDTO.getProductName());
				
				returnDetail.setPurchasePrice(returnDetailDTO.getPurchasePrice());
				
				returnDetail.setReturnedQuantity(returnDetailDTO.getReturnedQuantity());
				
				returnDetail.setSupplierId(returnDetailDTO.getSupplierId());
				
				returnDetail.setSupplierName(returnDetailDTO.getSupplierName());
				
				returnDetail.setSupplierPoId(returnDetailDTO.getSupplierPoId());
				
				returnDetail.setSupplierPoItemId(returnDetailDTO.getSupplierPoItemId());
				
				returnDetail.setInboundId(returnDetailDTO.getInboundId());
				
				returnDetail.setWarehouseId(returnDetailDTO.getWarehouseId());
				
				returnDetail.setWarehouseName(returnDetailDTO.getWarehouseName());
				
				returnDetail.setUom(returnDetailDTO.getUom());
				
				returnDetail.setTax(returnDetailDTO.getTax());
				
				returnDetails.add(returnDetail);
			}
		}

		return returnDetails;
	}

	@Override
	@Transactional
	public List<Packet> findByInvoiceNumbersIn(List<String> invoiceNumbers) {
		 List <Packet> packets = packetRepository.findByInvoiceNumberIn(invoiceNumbers);
		return packets;
	}

	@Override
	public BaseResponse markScanned(Integer emsPacketId) {
		logger.info("mark scanning start for ems packet id :" + emsPacketId);
		BaseResponse response = new BaseResponse();
		Packet packet = packetRepository.findByEmsPacketId(emsPacketId).orElse(null);
		if (packet == null ) {
			response.setMessage("No Packet Details found for id: " + emsPacketId);
			response.setStatus(false);
		} else {
			if (packet.getStatus().equals(PacketStatus.PICKUPLIST_DONE)
					|| packet.getStatus().equals(PacketStatus.INVOICED)) {
				packet.setStatus(PacketStatus.SCANNED);
				packetRepository.save(packet);
				response.setMessage("Packet marked successfully for id: " + emsPacketId);
				response.setStatus(true);
			} else {
				response.setMessage("Packet already scanned  for id: " + emsPacketId);
				response.setStatus(false);
			}
		}
		response.setCode(HttpStatus.OK.value());
		logger.info("mark scanning done for ems packet id :" + emsPacketId);
		return response;
	}
	
	@Override
	public PacketLotResponse getLotInfo(@Valid PacketLotInfoRequest request, Pageable page) {
		List<LotInfo> lotInformation = packetRepository.findLotInformatonByInvoiceNumbers(request.getInvoiceNumbers());
		
		if(lotInformation.isEmpty()) {
			PacketLotResponse response = new PacketLotResponse(
					"Not found any lot infomation against the invoice numbers", false, HttpStatus.OK.value());
			
			return response;
		}else {
			PacketLotResponse response = new PacketLotResponse(
					"Found " + lotInformation.size() + " records for invoice numbers", true, HttpStatus.OK.value());
			
			response.setLotInfo(lotInformation);
			
			return response;
		}
	}

	//App
	@Override
	public MSNListResponse getMSNList(MSNListRequest request) {
		
		Packet packet = packetRepository.findById(request.getPacketId()).orElse(null);
		if(packet==null) {
			return new MSNListResponse("No Packet Found for Packet Id :: "+request.getPacketId(),false,200);
		}
		
		List<MSNListDTO> msnListDto;

		msnListDto=packetRepository.findMSNListusingPacketId(request.getPacketId());
		if(msnListDto!=null) {
		     logger.info("MSN List found for PacketId ::" + request.getPacketId());
		}else {
		     logger.info("MSN List Found Empty for packetId::" + request.getPacketId());
		}
		MSNListResponse response=new MSNListResponse("MSN List Found for PacketId  :: "+ request.getPacketId(),true,200);
		response.setMsnListDto(msnListDto);
		
		
		return response;
	}

	@Override
	public SearchPacketForPickupResponse appSearchPacketForPickup(SearchPacketForPickupRequest request, Pageable page,
			String authName) {
		logger.info("Search Packet for Pickup Service Started");
		SearchPacketForPickupResponse response;
		Page<Packet> packets;

		 if ( request.getInvoiceNumber() != null) {
			logger.info("Search Packet for Pickup Service Started" + request.toString());
			PacketStatus status = request.getStatus();
			packets = packetRepository.findByWarehouseIdAndInvoiceNumberAndStatusNotOrderByIdAsc(
					request.getWarehouseId(), request.getInvoiceNumber(), PacketStatus.CANCELLED, page);
		 

		List<PacketDto> packetDtos = new ArrayList<>();
		if (!packets.getContent().isEmpty()) {
			for (Packet packet : packets) {
				if(packets.getContent().get(0).getPickedby()==null) {
					packetDtos.add(new PacketDto(packet,true));
				}
				else if (packets.getContent().get(0).getPickedby().equals(authName)) {
					String username=packets.getContent().get(0).getPickedby();
					username=username.substring(0, username.indexOf("@"));
					username=username.replace('.',' ');
					packetDtos.add(new PacketDto(packet,true));
				}else {
					String username=packets.getContent().get(0).getPickedby();
					username=username.substring(0, username.indexOf("@"));
					username=username.replace('.',' ');
					packetDtos.add(new PacketDto(packet,false,username));
				}
				
			}
			response = (SearchPacketForPickupResponse) PaginationUtil.setPaginationParams(packets,
					new SearchPacketForPickupResponse("Packets found : " + packets.getTotalElements(), true,
							HttpStatus.OK.value()));
			
			response.setPackets(packetDtos);
		} else {
			response = new SearchPacketForPickupResponse(
					"No Packet found for warehouse id: " + request.getWarehouseId(), true, HttpStatus.OK.value());
		}
		}else {
			response = new SearchPacketForPickupResponse(
					"No Packet found for warehouse id: " + request.getWarehouseId(), true, HttpStatus.OK.value());
		
		}
		logger.info("Search Packet for Pickup Service Ended");
		return response;
	}
		 
	}


