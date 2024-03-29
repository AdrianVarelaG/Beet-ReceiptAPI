package com.beet.receipt.application.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.beet.receipt.application.ReceiptService;
import com.beet.receipt.exception.InternalException;
import com.beet.receipt.exception.ReceiptException;
import com.beet.receipt.exception.ValidationException;
import com.beet.receipt.model.entity.Receipt;
import com.beet.receipt.model.entity.Ticket;
import com.beet.receipt.model.value.ReceiptStatus;
import com.beet.receipt.repository.ReceiptRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ReceiptServiceImpl implements ReceiptService {

	private ReceiptRepository receiptRepository;
	
	@Autowired
	public ReceiptServiceImpl(ReceiptRepository receiptRepository) {
		this.receiptRepository = receiptRepository;
	}
	
	@Override
	public Receipt createFromTicket(String account, Ticket ticket) {
		
		Receipt receipt = new Receipt();
		receipt.setAccount(account);
		receipt.setStatus(ReceiptStatus.IN_PROGRESS);
		receipt.setTicket(ticket);
		Receipt ret = this.receiptRepository.save(receipt);
		
		return ret;
	}

	@Override
	public Receipt findById(String account, Long id) throws ReceiptException {	
		try {
			Receipt r = this.receiptRepository.findByIdAndAccount(id, account);
			if(r == null)
				throw new ValidationException("Receipt not found");
			return r;
		}catch(ValidationException e) {
			log.error("prefix: {} id: {}" +  id, account, e);
			throw e;
		}catch(Exception e) {
			log.error("prefix: {} id: {}" + e.getMessage(), id, account, e);
			throw new InternalException();
		}
	}

	@Override
	public Page<Receipt> findByAccount(String account, Pageable pageable) {
		return this.receiptRepository.findByAccount(account, pageable);
	}

}
