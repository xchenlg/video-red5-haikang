package chen.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import chen.domain.EasyUIDataGrid;
import chen.domain.PageHelper;
import chen.domain.TSdk;
import chen.domain.Tree;
import chen.repository.SdkRepository;

@Service
public class SdkService {

	@Autowired
	private SdkRepository sdkRepository;

	public List<TSdk> findByIp(String ip, String port) {
		return sdkRepository.findByIpAndPort(ip, port);
	}

	public EasyUIDataGrid dataGrid(PageHelper ph) {
		EasyUIDataGrid dg = new EasyUIDataGrid();

		Sort sort = new Sort(Direction.ASC, "sdkOrder");
		Pageable pageable = new PageRequest(ph.getPage() - 1, ph.getRows(), sort);
		dg.setRows(sdkRepository.findAll(pageable).getContent());
		dg.setTotal(sdkRepository.count());
		return dg;
	}

	public void add(TSdk sdk) {
		sdkRepository.save(sdk);
	}

	public TSdk findById(String id) {
		return sdkRepository.findOne(Long.valueOf(id));
	}

	public void delete(String id) {
		sdkRepository.delete(Long.valueOf(id));
	}

	public Integer findMaxOrder() {
		return sdkRepository.findMaxOrder();
	}

	public void moveUpOrDown(String id, String flag) {
		TSdk sdk = sdkRepository.findOne(Long.valueOf(id));
		boolean isExcit = true;
		int i = 0;
		List<TSdk> dLisdt = new ArrayList<TSdk>();
		// flag == "0"向上移动 ;flag == "1" 向下移动
		if ("0".equals(flag)) {

			int max = sdkRepository.findMinOrder();
			if (sdk.getSdkOrder() != max) {
				while (isExcit) {
					i++;
					dLisdt = sdkRepository.findBySdkOrder(sdk.getSdkOrder() - i);
					if (dLisdt != null && dLisdt.size() > 0) {
						isExcit = false;
					}
				}
				for (TSdk tsdk : dLisdt) {
					tsdk.setSdkOrder(sdk.getSdkOrder());
					sdkRepository.save(tsdk);
				}
				sdk.setSdkOrder(sdk.getSdkOrder() - 1);
				sdkRepository.save(sdk);
			}

		} else {

			int min = sdkRepository.findMaxOrder();
			if (sdk.getSdkOrder() != min) {
				while (isExcit) {
					i++;
					dLisdt = sdkRepository.findBySdkOrder(sdk.getSdkOrder() + i);
					if (dLisdt != null && dLisdt.size() > 0) {
						isExcit = false;
					}
				}
				for (TSdk tsdk : dLisdt) {
					tsdk.setSdkOrder(sdk.getSdkOrder());
					sdkRepository.save(tsdk);
				}
				sdk.setSdkOrder(sdk.getSdkOrder() + 1);
				sdkRepository.save(sdk);
			}
		}
	}

	public List<Tree> getSdkTree() {

		List<TSdk> list = sdkRepository.findAllOrderByOrder();
		List<Tree> treeList = new ArrayList<Tree>();

		if (list != null && list.size() > 0) {
			for (TSdk t : list) {
				Tree tree = new Tree();
				tree.setId(t.getId() + "");
				tree.setText(t.getTitle());
				treeList.add(tree);
			}
			return treeList;
		}
		return null;
	}

	public List<TSdk> findAll() {
		return (List<TSdk>) sdkRepository.findAllOrderByOrder();
	}
}
