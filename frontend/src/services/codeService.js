import { apiClient } from './apiClient';

export const runCode = async (problemId, language, code) => {
    return await apiClient('/api/code/run', {
        method: 'POST',
        body: JSON.stringify({
            problemId,
            language,
            code,
        }),
    });
};