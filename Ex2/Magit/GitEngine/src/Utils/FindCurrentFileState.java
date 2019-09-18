package Utils;

public enum FindCurrentFileState implements FileState{
    UPDATED_IN_FIRST_ONLY {
        public boolean isItMe(boolean inFirst, boolean inSecond, boolean inAncestor,
                              boolean firstSha1EqualsSecond, boolean secondSha1EqualsAncestor,
                              boolean firstSha1EqualsAncestor) {
            return (inFirst && inSecond && inAncestor &&
                    !firstSha1EqualsSecond && secondSha1EqualsAncestor && !firstSha1EqualsAncestor);
        }

        @Override
        public void merge(String filePath, MergeResult res, String headBranchName, String branchToMergeName,
                          String ObjectsPath, String firstSha1, String secondSha1, String ancestorSha1) {
            MagitUtils.success(res, filePath);
            String resMsg = String.format("File %s was originally in ancestor, " +
                    "did not change in branch \"%s\" and was updated in branch %s.\n" +
                    "There was no conflict therefor added the file to the repository successfully!",
                    res.getFileName(), branchToMergeName, headBranchName);
            res.setSuccessMsg(resMsg);
        }
    },

    UPDATED_IN_FIRST_AND_SECOND {
        @Override
        public boolean isItMe(boolean inFirst, boolean inSecond, boolean inAncestor,
                              boolean firstSha1EqualsSecond, boolean secondSha1EqualsAncestor,
                              boolean firstSha1EqualsAncestor) {
            return (inFirst && inSecond && inAncestor &&
                    !firstSha1EqualsSecond && !secondSha1EqualsAncestor && !firstSha1EqualsAncestor);
        }

        @Override
        public void merge(String filePath, MergeResult res, String headBranchName, String branchToMergeName,
                          String ObjectsPath, String firstSha1, String secondSha1, String ancestorSha1) {
            MagitUtils.conflict(res, filePath);
            String resMsg = String.format("File %s was originally in ancestor, " +
                            "was updated in branch \"%s\" and in branch \"%s\".\n" +
                            "There is a conflict!",
                    res.getFileName(), headBranchName, branchToMergeName);
            res.setConflictMsg(resMsg);
        }
    },

    UPDATED_IN_SECOND_ONLY {
        @Override
        public boolean isItMe(boolean inFirst, boolean inSecond, boolean inAncestor,
                              boolean firstSha1EqualsSecond, boolean secondSha1EqualsAncestor,
                              boolean firstSha1EqualsAncestor) {
            return (inFirst && inSecond && inAncestor &&
                    !firstSha1EqualsSecond && !secondSha1EqualsAncestor && firstSha1EqualsAncestor);
        }

        @Override
        public void merge(String filePath, MergeResult res, String headBranchName, String branchToMergeName,
                          String ObjectsPath, String firstSha1, String secondSha1, String ancestorSha1) {
            MagitUtils.writeFileToRepository(filePath, MagitUtils.joinPaths(ObjectsPath, secondSha1), res);
            String resMsg = String.format("File %s was originally in ancestor, " +
                            "was updated in branch \"%s\" and did not change in branch \"%s\".\n" +
                            "There was no conflict therefor added the file to the repository successfully!",
                    res.getFileName(), branchToMergeName, headBranchName);
            res.setSuccessMsg(resMsg);
        }
    },

    WAS_NOT_UPDATED_IN_ANY_OF_THEM {
        @Override
        public boolean isItMe(boolean inFirst, boolean inSecond, boolean inAncestor,
                              boolean firstSha1EqualsSecond, boolean secondSha1EqualsAncestor,
                              boolean firstSha1EqualsAncestor) {
            return (inFirst && inSecond && inAncestor &&
                    firstSha1EqualsSecond && secondSha1EqualsAncestor && firstSha1EqualsAncestor);
        }

        @Override
        public void merge(String filePath, MergeResult res, String headBranchName, String branchToMergeName,
                          String ObjectsPath, String firstSha1, String secondSha1, String ancestorSha1) {
            MagitUtils.success(res, filePath);
            String resMsg = String.format("File %s was originally in ancestor, " +
                            "did not change in branch \"%s\" and in branch \"%s\".\n" +
                            "There was no conflict therefor file stayed the same in repository!",
                    res.getFileName(), branchToMergeName, headBranchName);
            res.setSuccessMsg(resMsg);
        }
    },

    DELETED_IN_FIRST_SAME_IN_SECOND {
        @Override
        public boolean isItMe(boolean inFirst, boolean inSecond, boolean inAncestor,
                              boolean firstSha1EqualsSecond, boolean secondSha1EqualsAncestor,
                              boolean firstSha1EqualsAncestor) {
            return (!inFirst && inSecond && inAncestor &&
                    !firstSha1EqualsSecond && secondSha1EqualsAncestor && !firstSha1EqualsAncestor);
        }

        @Override
        public void merge(String filePath, MergeResult res, String headBranchName, String branchToMergeName,
                          String ObjectsPath, String firstSha1, String secondSha1, String ancestorSha1) {
            MagitUtils.success(res, filePath);
            String resMsg = String.format("File %s was originally in ancestor, " +
                            "did not change in branch \"%s\" and was deleted in branch \"%s\".\n" +
                            "There was no conflict therefor deleted the file from repository successfully!",
                    res.getFileName(), branchToMergeName, headBranchName);
            res.setSuccessMsg(resMsg);
        }
    },

    DELETED_IN_FIRST_CHANGED_IN_SECOND {
        @Override
        public boolean isItMe(boolean inFirst, boolean inSecond, boolean inAncestor,
                              boolean firstSha1EqualsSecond, boolean secondSha1EqualsAncestor,
                              boolean firstSha1EqualsAncestor) {
            return (!inFirst && inSecond && inAncestor &&
                    !firstSha1EqualsSecond && !secondSha1EqualsAncestor && !firstSha1EqualsAncestor);
        }

        @Override
        public void merge(String filePath, MergeResult res, String headBranchName, String branchToMergeName,
                          String ObjectsPath, String firstSha1, String secondSha1, String ancestorSha1) {
            MagitUtils.conflict(res, filePath);
            String resMsg = String.format("File %s was originally in ancestor, " +
                            "was updated in branch \"%s\" and was deleted in branch \"%s\".\n" +
                            "There is a conflict!",
                    res.getFileName(), branchToMergeName, headBranchName);
            res.setConflictMsg(resMsg);
        }
    },

    DELETED_IN_SECOND_SAME_IN_FIRST {
        @Override
        public boolean isItMe(boolean inFirst, boolean inSecond, boolean inAncestor,
                              boolean firstSha1EqualsSecond, boolean secondSha1EqualsAncestor,
                              boolean firstSha1EqualsAncestor) {
            return (inFirst && !inSecond && inAncestor &&
                    !firstSha1EqualsSecond && !secondSha1EqualsAncestor && firstSha1EqualsAncestor);
        }

        @Override
        public void merge(String filePath, MergeResult res, String headBranchName, String branchToMergeName,
                          String ObjectsPath, String firstSha1, String secondSha1, String ancestorSha1) {
            MagitUtils.deleteFileFromRepository(filePath, res);
            String resMsg = String.format("File %s was originally in ancestor, " +
                            "was deleted in branch \"%s\" and did not change in branch \"%s\".\n" +
                            "There was no conflict therefor deleted the file from repository successfully!",
                    res.getFileName(), branchToMergeName, headBranchName);
            res.setSuccessMsg(resMsg);
        }
    },

    DELETED_IN_SECOND_CHANGED_IN_FIRST {
        @Override
        public boolean isItMe(boolean inFirst, boolean inSecond, boolean inAncestor,
                              boolean firstSha1EqualsSecond, boolean secondSha1EqualsAncestor,
                              boolean firstSha1EqualsAncestor) {
            return (inFirst && !inSecond && inAncestor &&
                    !firstSha1EqualsSecond && !secondSha1EqualsAncestor && !firstSha1EqualsAncestor);
        }

        @Override
        public void merge(String filePath, MergeResult res, String headBranchName, String branchToMergeName,
                          String ObjectsPath, String firstSha1, String secondSha1, String ancestorSha1) {
            MagitUtils.conflict(res, filePath);
            String resMsg = String.format("File %s was originally in ancestor, " +
                            "was deleted in branch \"%s\" and was updated in branch \"%s\".\n" +
                            "There is a conflict!",
                    res.getFileName(), branchToMergeName, headBranchName);
            res.setConflictMsg(resMsg);
        }
    },

    DELETED_IN_FIRST_AND_SECOND {
        @Override
        public boolean isItMe(boolean inFirst, boolean inSecond, boolean inAncestor,
                              boolean firstSha1EqualsSecond, boolean secondSha1EqualsAncestor,
                              boolean firstSha1EqualsAncestor) {
            return (!inFirst && !inSecond && inAncestor &&
                    !firstSha1EqualsSecond && !secondSha1EqualsAncestor && !firstSha1EqualsAncestor);
        }

        @Override
        public void merge(String filePath, MergeResult res, String headBranchName, String branchToMergeName,
                          String ObjectsPath, String firstSha1, String secondSha1, String ancestorSha1) {
            MagitUtils.success(res, filePath);
            String resMsg = String.format("File %s was originally in ancestor, " +
                            "was deleted in branch \"%s\" and branch \"%s\".\n" +
                            "There was no conflict therefor deleted the file from repository successfully!",
                    res.getFileName(), branchToMergeName, headBranchName);
            res.setSuccessMsg(resMsg);
        }
    },

    SAME_FILE_ADDED_IN_FIRST_AND_IN_SECOND {
        @Override
        public boolean isItMe(boolean inFirst, boolean inSecond, boolean inAncestor,
                              boolean firstSha1EqualsSecond, boolean secondSha1EqualsAncestor,
                              boolean firstSha1EqualsAncestor) {
            return (inFirst && inSecond && !inAncestor &&
                    firstSha1EqualsSecond && !secondSha1EqualsAncestor && !firstSha1EqualsAncestor);
        }

        @Override
        public void merge(String filePath, MergeResult res, String headBranchName, String branchToMergeName,
                          String ObjectsPath, String firstSha1, String secondSha1, String ancestorSha1) {
            MagitUtils.success(res, filePath);
            String resMsg = String.format("File %s wasn't originally in ancestor, " +
                            "same version was added to branch \"%s\" and branch \"%s\".\n" +
                            "There was no conflict therefor added the file to the repository successfully!",
                    res.getFileName(), branchToMergeName, headBranchName);
            res.setSuccessMsg(resMsg);
        }
    },

    DIFFERENT_FILE_ADDED_IN_FIRST_AND_IN_SECOND {
        @Override
        public boolean isItMe(boolean inFirst, boolean inSecond, boolean inAncestor,
                              boolean firstSha1EqualsSecond, boolean secondSha1EqualsAncestor,
                              boolean firstSha1EqualsAncestor) {
            return (inFirst && inSecond && !inAncestor &&
                    !firstSha1EqualsSecond && !secondSha1EqualsAncestor && !firstSha1EqualsAncestor);
        }

        @Override
        public void merge(String filePath, MergeResult res, String headBranchName, String branchToMergeName,
                          String ObjectsPath, String firstSha1, String secondSha1, String ancestorSha1) {
            MagitUtils.conflict(res, filePath);
            String resMsg = String.format("File %s wasn't originally in ancestor, " +
                            "different versions were added to branch \"%s\" and branch \"%s\".\n" +
                            "There is a conflict!",
                    res.getFileName(), branchToMergeName, headBranchName);
            res.setConflictMsg(resMsg);
        }
    },

    ADDED_IN_FIRST_ONLY {
        @Override
        public boolean isItMe(boolean inFirst, boolean inSecond, boolean inAncestor,
                              boolean firstSha1EqualsSecond, boolean secondSha1EqualsAncestor,
                              boolean firstSha1EqualsAncestor) {
            return (inFirst && !inSecond && !inAncestor &&
                    !firstSha1EqualsSecond && !secondSha1EqualsAncestor && !firstSha1EqualsAncestor);
        }

        @Override
        public void merge(String filePath, MergeResult res, String headBranchName, String branchToMergeName,
                          String ObjectsPath, String firstSha1, String secondSha1, String ancestorSha1) {
            MagitUtils.success(res, filePath);
            String resMsg = String.format("File %s wasn't originally in ancestor, " +
                            "wasn't added to branch \"%s\" and was added to branch \"%s\".\n" +
                            "There was no conflict therefor added the file to the repository successfully!",
                    res.getFileName(), branchToMergeName, headBranchName);
            res.setSuccessMsg(resMsg);
        }
    },

    ADDED_IN_SECOND_ONLY {
        @Override
        public boolean isItMe(boolean inFirst, boolean inSecond, boolean inAncestor,
                              boolean firstSha1EqualsSecond, boolean secondSha1EqualsAncestor,
                              boolean firstSha1EqualsAncestor) {
            return (!inFirst && inSecond && !inAncestor &&
                    !firstSha1EqualsSecond && !secondSha1EqualsAncestor && firstSha1EqualsAncestor);
        }

        @Override
        public void merge(String filePath, MergeResult res, String headBranchName, String branchToMergeName,
                          String ObjectsPath, String firstSha1, String secondSha1, String ancestorSha1) {
            MagitUtils.writeFileToRepository(filePath, MagitUtils.joinPaths(ObjectsPath, secondSha1), res);
            String resMsg = String.format("File %s wasn't originally in ancestor, " +
                            "was added to branch \"%s\" and wasn't added to branch \"%s\".\n" +
                            "There was no conflict therefor added the file to the repository successfully!",
                    res.getFileName(), branchToMergeName, headBranchName);
            res.setSuccessMsg(resMsg);
        }
    }
}
