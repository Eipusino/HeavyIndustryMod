package heavyindustry.android.classes.dexmaker;

import com.android.dx.rop.code.*;
import com.android.dx.util.*;

import java.util.*;

public class BlockHead {
    private final List<Insn> insns = new ArrayList<>();
    private final List<BlockHead> branches = new ArrayList<>();

    public boolean isAdded;
    public int labelId;

    private BlockHead primaryBranch;

    public void addInsn(Insn insn) {
        insns.add(insn);
    }

    public BasicBlock toBlock() {
        InsnList list = new InsnList(insns.size());
        IntList successors = new IntList();

        for (int i = 0; i < insns.size(); i++) {
            list.set(i, insns.get(i));
        }

        for (BlockHead branch : branches) {
            successors.add(branch.labelId);
        }
        successors.add(primaryBranch.labelId);

        return new BasicBlock(
                labelId,
                list,
                successors,
                primaryBranch.labelId
        );
    }
}
