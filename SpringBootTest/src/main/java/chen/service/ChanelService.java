package chen.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import chen.domain.EasyUIDataGrid;
import chen.domain.PChanel;
import chen.domain.PageHelper;
import chen.domain.TChanel;
import chen.repository.ChanelRepository;

@Service
public class ChanelService {

	@Autowired
	private ChanelRepository chanelRepository;

	public List<TChanel> findByIp(String ip) {
		return chanelRepository.findBySdk_IpOrderByChanelOrderAsc(ip);
	}

	public EasyUIDataGrid dataGrid(Long id, PageHelper ph) {
		EasyUIDataGrid dg = new EasyUIDataGrid();

		Sort sort = new Sort(Direction.ASC, "chanelOrder");
		Pageable pageable = new PageRequest(ph.getPage() - 1, ph.getRows(), sort);
		List<TChanel> tchanels = chanelRepository.findBySdk_IdOrderByChanelOrderAsc(id, pageable).getContent();

		List<PChanel> chanels = new ArrayList<>();

		for (TChanel t : tchanels) {
			PChanel p = new PChanel();
			BeanUtils.copyProperties(t, p);
			p.setSdkId(t.getSdk().getId());
			chanels.add(p);
		}

		dg.setRows(chanels);
		dg.setTotal(chanelRepository.count());
		return dg;
	}

	public void add(TChanel sdk) {
		chanelRepository.save(sdk);
	}

	public PChanel findById(String id) {
		PChanel p = new PChanel();
		TChanel t = chanelRepository.findOne(Long.valueOf(id));
		BeanUtils.copyProperties(t, p);
		p.setSdkId(t.getSdk().getId());
		return p;
	}

	public void delete(String id) {
		chanelRepository.delete(Long.valueOf(id));
	}

	public Integer findMaxOrder(Long sdkid) {
		return chanelRepository.findMaxOrder(sdkid);
	}

	public void moveUpOrDown(String id, String flag, Long sdkid) {
		TChanel sdk = chanelRepository.findOne(Long.valueOf(id));
		boolean isExcit = true;
		int i = 0;
		List<TChanel> dLisdt = new ArrayList<TChanel>();
		// flag == "0"向上移动 ;flag == "1" 向下移动
		if ("0".equals(flag)) {

			int max = chanelRepository.findMinOrder(sdkid);
			if (sdk.getChanelOrder() != max) {
				while (isExcit) {
					i++;
					dLisdt = chanelRepository.findByChanelOrderAndSdk_Id(sdk.getChanelOrder() - i, sdkid);
					if (dLisdt != null && dLisdt.size() > 0) {
						isExcit = false;
					}
				}
				for (TChanel TChanel : dLisdt) {
					TChanel.setChanelOrder(sdk.getChanelOrder());
					chanelRepository.save(TChanel);
				}
				sdk.setChanelOrder(sdk.getChanelOrder() - 1);
				chanelRepository.save(sdk);
			}

		} else {

			int min = chanelRepository.findMaxOrder(sdkid);
			if (sdk.getChanelOrder() != min) {
				while (isExcit) {
					i++;
					dLisdt = chanelRepository.findByChanelOrderAndSdk_Id(sdk.getChanelOrder() + i, sdkid);
					if (dLisdt != null && dLisdt.size() > 0) {
						isExcit = false;
					}
				}
				for (TChanel TChanel : dLisdt) {
					TChanel.setChanelOrder(sdk.getChanelOrder());
					chanelRepository.save(TChanel);
				}
				sdk.setChanelOrder(sdk.getChanelOrder() + 1);
				chanelRepository.save(sdk);
			}
		}
	}

	public List<PChanel> findBySdk_IdOrderByChanelOrderAsc(String id) {
		
		List<PChanel> chanels = new ArrayList<>();

		List<TChanel> tchanels = chanelRepository.findBySdk_IdOrderByChanelOrderAsc(Long.valueOf(id));
		for (TChanel t : tchanels) {
			PChanel p = new PChanel();
			BeanUtils.copyProperties(t, p);
			p.setSdkId(t.getSdk().getId());
			chanels.add(p);
		}
		
		return chanels;
	}
}
