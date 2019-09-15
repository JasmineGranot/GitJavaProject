package Utils;

public interface FileState {
    boolean isItMe(boolean inFirst, boolean inSecond, boolean inAncestor,
                          boolean firstSha1EqualsSecond, boolean secondSha1EqualsAncestor,
                          boolean firstSha1EqualsAncestor);

    void merge(String filePath, MergeResult res, String headBranchName, String branchToMergeName,
               String ObjectsPath, String firstSha1, String secondSha1, String ancestorSha1);

}
